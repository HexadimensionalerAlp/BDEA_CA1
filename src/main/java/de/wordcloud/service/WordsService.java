package de.wordcloud.service;

import de.wordcloud.database.entity.WordsEntity;
import de.wordcloud.database.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordsService implements IFWordsService {
    @Autowired
    private WordsRepository repository;

    @Override
    public WordsEntity insert(String word, int count, int documentId) {
        WordsEntity we = new WordsEntity();
        we.setWord(word);
        we.setCount(count);
        we.setDocumentId(documentId);
        we.setTf(null);
        we.setIdf(null);
        we.setTfidf(null);
        return repository.save(we);
    }

    @Override
    public WordsEntity update(int id, WordsEntity newEntity) {
        WordsEntity oldEntity = repository.findById(id).orElse(null);
        //die 3 eig useless für uns
        oldEntity.setWord(newEntity.getWord());
        oldEntity.setCount(newEntity.getCount());
        oldEntity.setDocumentId(newEntity.getDocumentId());
        //----------------------------
        oldEntity.setTf(newEntity.getTf());
        oldEntity.setIdf(newEntity.getIdf());
        oldEntity.setTfidf(newEntity.getTfidf());
        return repository.save(oldEntity);
    }

    @Override
    public WordsEntity getWord(int id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<WordsEntity> getAllWords() {
        return repository.findAll();
    }

    @Override
    public void delete(int id) {
        repository.delete(getWord(id));
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    //nur verwenden falls tf schon berechnet wurde
    public double tfidf(WordsEntity we) {

        //tf = anzahl wort / anzahl alle wörter

        //idf = log(anzahl aller dokumente / wort in x mal dokumenten enthalten)
        int x = repository.getNumberOfDocuments();
        int y = repository.getNumberOfWordInDocuments(we.getWord());
        double idf = Math.log10(x / y);

        double tfidf = we.getTf() * idf;

        we.setIdf(idf);
        we.setTfidf(tfidf);
        update(we.getId(), we);

        return tfidf;

    }


}
