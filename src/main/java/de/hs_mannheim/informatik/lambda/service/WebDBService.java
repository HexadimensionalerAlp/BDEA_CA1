package de.hs_mannheim.informatik.lambda.service;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebDBService {
    public final static String TAG_CLOUD_PATH = "tagclouds/";

    private final DBORMMock dbORM = new DBORMMock();

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

    public DocumentMockEntity getDocumentByName(String name) {
        return null;
    }

    public void createTagCloud(DocumentMockEntity document) {
        final Dimension dimension = new Dimension(600, 600);
        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(8, 50));
        wordCloud.build(document.words);
        wordCloud.writeToFile(TAG_CLOUD_PATH + document.name + ".png");
    }

    public void createTagClouds() {
        final ArrayList<DocumentMockEntity> documents = dbORM.select("");
        
        if (documents != null) {
            for (var document : documents) {
                createTagCloud(document);
            }
        }
    }

    public void createGlobalTagCloud() {
        final ArrayList<WordFrequency> words = dbORM.select("");

        createTagCloud(new DocumentMockEntity("global", words));
    }
}


class DocumentMockEntity {
    public String name;
    public ArrayList<WordFrequency> words = new ArrayList<>();

    public DocumentMockEntity(String name, ArrayList<WordFrequency> words) {
        this.name = name;
        this.words = words;
    }
}

class DBORMMock {
    public ArrayList select(String query) {
        return null;
    }
}