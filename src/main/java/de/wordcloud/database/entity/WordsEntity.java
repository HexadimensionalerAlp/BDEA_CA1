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
    @Column(name = "documentId", nullable = false)
    private Integer documentId;

    @Id
    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "wordCount", nullable = false)
    private Integer wordCount;

    @Column(name = "tf", nullable = false)
    private Double tf;
}
