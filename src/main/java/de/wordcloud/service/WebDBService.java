package de.wordcloud.service;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import de.wordcloud.database.repository.DocumentsRepository;
import de.wordcloud.database.repository.GlobalWordsRepository;
import de.wordcloud.database.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class WebDBService {
    public final static String TAG_CLOUD_PATH = "tagclouds/";

    @Autowired
    private WordsRepository wordsRepository;

    @Autowired
    private GlobalWordsRepository globalWordsRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    public ArrayList<String> listAllTagClouds() {
        File[] files = new File(TAG_CLOUD_PATH).listFiles();
        ArrayList<String> tagClouds = new ArrayList<>();

        if (files != null) {
            for (var file : files) {
                if (file.getName().endsWith(".png")) {
                    tagClouds.add(TAG_CLOUD_PATH + file.getName());
                }
            }
        }

        return tagClouds;
    }

    public void createTagCloud(ArrayList<WordFrequency> wordFrequencies, String documentName) {
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(8, 50));
        wordCloud.build(wordFrequencies);
        wordCloud.writeToFile(TAG_CLOUD_PATH + documentName + ".png");
    }

    public void createTagCloudForDocument(int documentId) {
        String documentName = this.documentsRepository.getDocumentName(documentId);
        ArrayList<WordFrequency> wordFrequencies = this.wordsRepository.getWordFrequencyOfDocument(documentId)
                .stream()
                .map(word -> new WordFrequency(word.getWord(), word.getTf())).collect(Collectors.toCollection(ArrayList::new));
        this.createTagCloud(wordFrequencies, documentName);
    }

    public void createTagClouds() {
        final ArrayList<Integer> documentIds = this.documentsRepository.getDocumentIds();
        
        if (documentIds != null) {
            for (var documentId : documentIds) {
                createTagCloudForDocument(documentId);
            }
        }
    }

    public void createGlobalTagCloud() {
        final ArrayList<WordFrequency> wordFrequencies = this.globalWordsRepository.getGlobalWordFrequency()
                .stream()
                .map(word -> new WordFrequency(word.getWord(), word.getTf())).collect(Collectors.toCollection(ArrayList::new));

        createTagCloud(wordFrequencies, "GlobalTagCloud");
    }
}
