package de.wordcloud.database.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "Words")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WordsEntity {
    @Id
    @Column(name = "document_id", nullable = false)
    private Integer documentId;

    @Column(name = "word_count", nullable = false)
    private Integer wordCount;

    @Id
    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "tf", nullable = false)
    private Integer tf;
}
