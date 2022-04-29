package de.wordcloud.database.repository;

import de.wordcloud.database.entity.DocumentEntity;
import de.wordcloud.database.entity.GlobalWordsEntity;
import de.wordcloud.database.entity.WordsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;


public interface DocumentsRepository extends JpaRepository<DocumentEntity, Integer> {

    @Query("SELECT id FROM DocumentEntity")
    ArrayList<Integer> getDocumentIds();

    @Query("SELECT name FROM DocumentEntity WHERE id = ?1")
    String getDocumentName(Integer documentId);

}
