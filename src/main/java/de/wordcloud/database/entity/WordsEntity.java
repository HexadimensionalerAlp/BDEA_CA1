package de.wordcloud.database.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "Words")
@Entity
@IdClass(WordsId.class)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WordsEntity implements Serializable {
    @Id
    @Column(name = "document_id", nullable = false)
    private Integer documentId;

    @Id
    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "word_count", nullable = false)
    private Integer wordCount;

    @Column(name = "tf", nullable = false)
    private Double tf;
}
