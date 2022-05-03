package de.wordcloud.service;

import de.wordcloud.database.entity.DocumentEntity;
import de.wordcloud.database.entity.GlobalWordsEntity;
import de.wordcloud.database.entity.WordsEntity;
import de.wordcloud.database.repository.DocumentsRepository;
import de.wordcloud.database.repository.GlobalWordsRepository;
import de.wordcloud.database.repository.WordsRepository;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StreamProcessingService {

    @Autowired
    private WordsRepository wordsRepository;

    @Autowired
    private GlobalWordsRepository globalWordsRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    public void process(DocumentEntity document) {
        String path = System.getProperty("user.dir") + "/" + WebService.FILES_PATH + document.getName();
        SparkConf config = new SparkConf().setAppName("WordCount").setMaster("local[*]");
        SparkSession spark = SparkSession.builder().config(config).getOrCreate();
        JavaSparkContext context = JavaSparkContext.fromSparkContext(spark.sparkContext());

        JavaRDD<String> text = context.textFile(path);
        JavaRDD<String> words = text.flatMap(content -> Arrays.asList(content.trim().split(" +")).iterator());
        JavaPairRDD<String, Integer> wordCountPairs = words
                .mapToPair(term -> new Tuple2(term.trim().toLowerCase().replaceAll("[^\\w\\-_äöüß]", ""), 1))
                .reduceByKey((x, y) -> (int) x + (int) y);
        JavaPairRDD<String, Integer> wordCountPairsWithoutStopWords = wordCountPairs.filter(pair -> pair._1.length() > 3);

        Map<String, Double> idfs = this.globalWordsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalWordsEntity::getWord, GlobalWordsEntity::getIdf));

        long documentCount = this.documentsRepository.count();

        JavaRDD<WordsEntity> finalRdd = wordCountPairsWithoutStopWords
                .map(wordPair -> {
                    double idf;
                    double tfidf;

                    if (idfs.containsKey(wordPair._1)) {
                        idf = idfs.get(wordPair._1);
                    } else {
                        idf = Math.log(documentCount); // documentCount / 1 (due to missing df)
                    }

                    tfidf = wordPair._2 * idf;

                    return new WordsEntity(document.getId(), wordPair._1, wordPair._2, tfidf);
                });

        ArrayList<WordsEntity> result = new ArrayList<>(finalRdd.collect());
        this.wordsRepository.saveAll(result);
    }
}
