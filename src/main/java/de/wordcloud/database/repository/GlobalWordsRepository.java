package de.wordcloud.database.repository;

import de.wordcloud.database.entity.GlobalWordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GlobalWordsRepository extends JpaRepository<GlobalWordsEntity, Integer> {

    @Query("SELECT new GlobalWordsEntity(word, tf, idf) FROM GlobalWordsEntity ORDER BY tf DESC")
    List<GlobalWordsEntity> getGlobalWordFrequency();

}
