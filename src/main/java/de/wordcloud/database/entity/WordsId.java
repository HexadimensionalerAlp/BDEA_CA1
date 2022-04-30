package de.wordcloud.database.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@AllArgsConstructor
@EqualsAndHashCode
public class WordsId  implements Serializable {
    private Integer documentId;
    private String word;
}
