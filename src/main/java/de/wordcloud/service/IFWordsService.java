package de.wordcloud.service;

import de.wordcloud.database.entity.WordsEntity;

import java.util.List;

public interface IFWordsService {

    public WordsEntity insert(String word, int count, int documentId);

    public WordsEntity update(int id, WordsEntity newEntity);

    public WordsEntity getWord(int id);

    public List<WordsEntity> getAllWords();

    public void delete(int id);

    public void deleteAll();

    public double tfidf(WordsEntity we);

}