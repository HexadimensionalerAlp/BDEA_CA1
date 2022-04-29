package de.wordcloud.test;

import de.wordcloud.database.entity.WordsEntity;
import de.wordcloud.service.IFWordsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class test {

    @Autowired
    private IFWordsService service;

    @Test
    @DisplayName("t1")
    public void insertTest() {
        service.deleteAll();

        WordsEntity we = service.insert("wort1", 5, 1);

        System.out.println("id of 'we': " + we.getId());
        Assertions.assertEquals(we.getId(), service.getWord(we.getId()).getId());

        WordsEntity we2 = service.insert("wort2", 3, 1);
        WordsEntity we3 = service.insert("wort3", 5, 1);

        WordsEntity we4 = service.insert("wort1", 10, 2);
        WordsEntity we5 = service.insert("wort4", 7, 2);

        //?tf kommt vom speed layer?
        we.setTf(5.0/13);
        we2.setTf(3.0/13);
        we3.setTf(5.0/13);
        we4.setTf(10.0/17);


        System.out.println("tfidf of 'we': " + service.tfidf(we));
        System.out.println("tfidf of 'we2': " + service.tfidf(we2));
        System.out.println("tfidf of 'we3': " + service.tfidf(we3));
        System.out.println("tfidf of 'we4': " + service.tfidf(we4));

    }

}
