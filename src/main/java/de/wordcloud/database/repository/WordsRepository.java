package de.wordcloud.database.repository;

import de.wordcloud.database.entity.WordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface WordsRepository extends JpaRepository<WordsEntity, Integer> {

    @Query("SELECT new WordsEntity(documentId, word, tf, tfidf) FROM WordsEntity WHERE documentId = ?1 ORDER BY tfidf DESC")
    List<WordsEntity> getWordFrequencyOfDocument(int documentId);

}
