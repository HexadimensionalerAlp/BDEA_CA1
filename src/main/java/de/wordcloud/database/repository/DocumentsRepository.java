package de.wordcloud.database.repository;

import de.wordcloud.database.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface DocumentsRepository extends JpaRepository<DocumentEntity, Integer> {

    @Query("SELECT name FROM DocumentEntity WHERE id = ?1")
    String getDocumentName(Integer documentId);

}
