package de.wordcloud.database.repository;

import de.wordcloud.database.entity.WordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface WordsRepository extends JpaRepository<WordsEntity, Integer> {

    @Query("SELECT COUNT(DISTINCT documentId) FROM WordsEntity")
    Integer getNumberOfDocuments();

    @Query("SELECT COUNT(documentId) FROM WordsEntity WHERE word = ?1 GROUP BY word")
    Integer getNumberOfWordInDocuments(String word);

}
