package de.wordcloud.service;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import de.wordcloud.database.entity.DocumentEntity;
import de.wordcloud.database.repository.DocumentsRepository;
import de.wordcloud.database.repository.GlobalWordsRepository;
import de.wordcloud.database.repository.WordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WebService {
    public final static String TAG_CLOUD_PATH = "tagclouds/";
    public final static String FILES_PATH = "files/";

    public final static int WORD_CLOUD_SIZE = 200;

    @Autowired
    private WordsRepository wordsRepository;

    @Autowired
    private GlobalWordsRepository globalWordsRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private final StreamProcessingService streamProcessing;

    public WebService(StreamProcessingService streamProcessing) {
        this.streamProcessing = streamProcessing;
    }

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

    public DocumentEntity saveFile(MultipartFile file) {
        DocumentEntity document = new DocumentEntity();
        try {
            Path filesPath = Paths.get(FILES_PATH);
            String fileName = file.getOriginalFilename();

            if (fileName != null && fileName.endsWith(".txt")) {
                if (!Files.exists(filesPath)) {
                    Files.createDirectory(filesPath);
                }

                if (Files.exists(filesPath.resolve(fileName))) {
                    String pureName = fileName.replaceFirst("\\.txt$", "");
                    fileName = pureName + "_" + System.currentTimeMillis() + file.hashCode() + ".txt";
                }

                Files.copy(file.getInputStream(), filesPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                document.setName(fileName);
                document = this.documentsRepository.save(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
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

    public void createTagCloudForDocument(long documentId) {
        String documentName = this.documentsRepository.getDocumentName(documentId);
        ArrayList<WordFrequency> wordFrequencies = this.wordsRepository.getWordFrequencyOfDocument(documentId)
                .stream()
                .limit(WORD_CLOUD_SIZE)
                .map(word -> new WordFrequency(word.getWord(), word.getTfidf().intValue()))
                .collect(Collectors.toCollection(ArrayList::new));
        this.createTagCloud(wordFrequencies, documentName);
    }

    public void createTagClouds() {
        final ArrayList<DocumentEntity> documents = new ArrayList<>(this.documentsRepository.findAll());

        for (var document : documents) {
            this.createTagCloudForDocument(document.getId());
        }
    }

    public void createGlobalTagCloud() {
        final ArrayList<WordFrequency> wordFrequencies = this.globalWordsRepository.getGlobalWordFrequency()
                .stream()
                .limit(WORD_CLOUD_SIZE)
                .map(word -> new WordFrequency(word.getWord(), word.getTfidf().intValue())).collect(Collectors.toCollection(ArrayList::new));

        createTagCloud(wordFrequencies, "GlobalTagCloud");
    }
}
