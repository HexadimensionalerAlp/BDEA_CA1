package de.wordcloud.service;

import de.wordcloud.database.DBUtils;
import de.wordcloud.database.entity.GlobalWordsEntity;
import de.wordcloud.database.entity.WordsEntity;
import de.wordcloud.database.repository.DocumentsRepository;
import de.wordcloud.database.repository.GlobalWordsRepository;
import de.wordcloud.database.repository.WordsRepository;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.count;
import static org.apache.spark.sql.functions.sum;

@Service
public class BatchProcessingService {

    @Autowired
    private GlobalWordsRepository globalWordsRepository;

    @Autowired
    private WordsRepository wordsRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    public void process() {
        SparkConf config = new SparkConf().setAppName("WordCount").setMaster("local[*]");
        SparkSession spark = SparkSession.builder().config(config).getOrCreate();
        Dataset<Row> allWords = spark.read()
                .jdbc("jdbc:mysql://localhost:3306", "bdea.words", DBUtils.getConnectionProperties());

        long documentCount = this.documentsRepository.count();

        Dataset<Row> aggregatedWords = allWords
                .select("document_id", "word", "tf")
                .groupBy("word")
                .agg(count("document_id"), sum("tf"))
                .orderBy("sum(tf)");

        Dataset<GlobalWordsEntity> transformedWords = aggregatedWords
                .map((MapFunction<Row, GlobalWordsEntity>) row -> {
                    long tf = row.<Long>getAs("sum(tf)");
                    double idf = Math.log((double) documentCount / (double)row.<Long>getAs("count(document_id)"))
                            / Math.log(2);
                    double tfidf = tf * idf;
                    return new GlobalWordsEntity(row.getAs("word"), tf, idf, tfidf);
                }, Encoders.bean(GlobalWordsEntity.class));

        transformedWords.write().mode("Overwrite")
                .jdbc("jdbc:mysql://localhost:3306", "bdea.global_words", DBUtils.getConnectionProperties());

        recalculateTfidfs(allWords);
    }

    private void recalculateTfidfs(Dataset<Row> allWords) {
        Map<String, Double> idfs = this.globalWordsRepository.findAll().stream()
                .collect(Collectors.toMap(GlobalWordsEntity::getWord, GlobalWordsEntity::getIdf));

        Dataset<WordsEntity> transformedWords = allWords
                .map((MapFunction<Row, WordsEntity>) row -> {
                    long tf = row.<Long>getAs("tf");
                    String word = row.getAs("word");
                    double idf = idfs.get(word);
                    double tfidf = tf * idf;
                    return new WordsEntity(row.<Long>getAs("document_id"), word, tf, tfidf);
                }, Encoders.bean(WordsEntity.class));

        ArrayList<WordsEntity> result = new ArrayList<>(transformedWords.toJavaRDD().collect());
        this.wordsRepository.saveAll(result);

        // for some reason this seems to only empty the table but not fill it with the actual words
        // transformedWords.write().mode("Overwrite")
        //         .jdbc("jdbc:mysql://localhost:3306", "bdea.words", DBUtils.getConnectionProperties());
    }
}
