package de.wordcloud.database.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "Words")
@Entity
@IdClass(WordsId.class)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WordsEntity implements Serializable {
    @Id
    @Column(name = "document_id", nullable = false)
    private Long document_id;

    @Id
    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "tf", nullable = false)
    private Long tf;

    @Column(name = "tfidf", nullable = false)
    private Double tfidf;
}
