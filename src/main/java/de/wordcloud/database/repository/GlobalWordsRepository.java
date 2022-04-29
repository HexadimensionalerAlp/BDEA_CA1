package de.wordcloud.database.repository;

import de.wordcloud.database.entity.GlobalWordsEntity;
import de.wordcloud.database.entity.WordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;


public interface GlobalWordsRepository extends JpaRepository<GlobalWordsEntity, Integer> {

    @Query("SELECT word, tfidf FROM GlobalWordsEntity ORDER BY tfidf DESC")
    ArrayList<GlobalWordsEntity> getGlobalWordFrequency();

}
