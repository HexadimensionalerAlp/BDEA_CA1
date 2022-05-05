package de.wordcloud.service;

import de.wordcloud.database.DBUtils;
import de.wordcloud.database.entity.DocumentEntity;
import de.wordcloud.database.entity.GlobalWordsEntity;
import de.wordcloud.database.entity.WordsEntity;
import de.wordcloud.database.repository.DocumentsRepository;
import de.wordcloud.database.repository.GlobalWordsRepository;
import org.apache.spark.sql.Dataset;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Encoders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StreamProcessingService {

    @Autowired
    private GlobalWordsRepository globalWordsRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    public void process(DocumentEntity document) {
        // change the path to use hdfs
        String path = System.getProperty("user.dir") + "/" + WebService.FILES_PATH + document.getName();
        SparkConf config = new SparkConf().setAppName("WordCount").setMaster("local[*]");
        SparkSession spark = SparkSession.builder().config(config).getOrCreate();
        JavaRDD<String> text;

        try (JavaSparkContext context = JavaSparkContext.fromSparkContext(spark.sparkContext())) {
            text = context.textFile(path);
            JavaRDD<String> words = text.flatMap(content ->
                    Arrays.asList(content.replaceAll("[-_]+", " ").split(" +")).iterator());
            JavaPairRDD<String, Integer> wordCountPairs = words
                    .mapToPair(term -> new Tuple2(term.trim().toLowerCase().replaceAll("[^\\wäöüß]", ""), 1))
                    .reduceByKey((x, y) -> (int) x + (int) y);
            JavaPairRDD<String, Integer> wordCountPairsWithoutStopWords = wordCountPairs.filter(pair -> pair._1.length() > 3);

            Map<String, Double> idfs = this.globalWordsRepository.findAll().stream()
                    .collect(Collectors.toMap(GlobalWordsEntity::getWord, GlobalWordsEntity::getIdf));

            long documentCount = this.documentsRepository.count();

            JavaRDD<WordsEntity> finalRdd = wordCountPairsWithoutStopWords
                    .map(wordPair -> {
                        double idf = idfs.containsKey(wordPair._1) ? idfs.get(wordPair._1) : Math.log(documentCount) / Math.log(2); // documentCount / 1 (due to missing df)
                        double tfidf = wordPair._2 * idf;

                        return new WordsEntity(document.getId(), wordPair._1, (long) wordPair._2, tfidf);
                    });

            Dataset<WordsEntity> wordsDataset = spark.sqlContext().createDataset(finalRdd.rdd(), Encoders.bean(WordsEntity.class));

            wordsDataset.write().mode("Append")
                    .jdbc("jdbc:mysql://localhost:3306", "bdea.words", DBUtils.getConnectionProperties());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
