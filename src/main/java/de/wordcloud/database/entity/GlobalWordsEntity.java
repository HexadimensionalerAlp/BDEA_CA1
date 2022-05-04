package de.wordcloud.database.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "GlobalWords")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class GlobalWordsEntity implements Serializable {
    @Id
    @Column(name = "word", nullable = false, unique = true)
    private String word;

    @Column(name = "tf", nullable = false)
    private Long tf;

    @Column(name = "idf", nullable = false)
    private Double idf;

    @Column(name = "tfidf", nullable = false)
    private Double tfidf;
}
