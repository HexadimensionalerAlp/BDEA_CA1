package de.wordcloud.database.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name = "GlobalWords")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GlobalWordsEntity {
    @Id
    @Column(name = "word", nullable = false, unique = true)
    private String word;

    @Column(name = "word_count", nullable = false)
    private Integer wordCount;

    @Column(name = "tf", nullable = false)
    private Double tf;

    @Column(name = "idf", nullable = false)
    private Double df;

    @Column(name = "tfidf", nullable = false)
    private Double tfidf;
}
