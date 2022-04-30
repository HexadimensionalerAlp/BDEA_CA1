package de.wordcloud.service;

import de.wordcloud.database.entity.DocumentEntity;
import de.wordcloud.database.entity.WordsEntity;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.springframework.stereotype.Service;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.Arrays;

@Service
public class StreamProcessingService {

    public void process(DocumentEntity document) {
        String path = WebService.FILES_PATH + document.getName();

        SparkConf config = new SparkConf().setAppName("WordCount").setMaster("local[*]");
        SparkSession spark = SparkSession.builder().config(config).getOrCreate();
        JavaSparkContext context = JavaSparkContext.fromSparkContext(spark.sparkContext());

        JavaRDD<String> text = context.textFile(path);
        JavaRDD<String> words = text.flatMap(content -> Arrays.asList(content.split(" ")).iterator());
        JavaPairRDD<String, Integer> wordCountPairs = words
                .mapToPair(tuple -> new Tuple2(tuple, 1))
                .reduceByKey((x, y) -> (int) x + (int) y);

        long wordCount = wordCountPairs.count();

        JavaRDD<WordsEntity> finalRdd = wordCountPairs
                .map(wordPair -> new WordsEntity(document.getId(), wordPair._1, wordPair._2, ((double)wordPair._2) / wordCount));

        finalRdd.foreach(System.out::println);
        // Dataset<Row> dataFrame = wordCount;
    }
}
