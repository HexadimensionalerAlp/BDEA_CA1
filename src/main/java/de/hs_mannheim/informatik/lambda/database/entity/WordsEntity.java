package de.hs_mannheim.informatik.lambda.database.entity;

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
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String word;

    private Integer count;

    private Integer documentId;

    private Double tf;
    private Double idf;
    private Double tfidf;


}
