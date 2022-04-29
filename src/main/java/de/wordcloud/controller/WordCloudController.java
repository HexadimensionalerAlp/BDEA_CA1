package de.wordcloud.controller;

import de.wordcloud.service.WebDBService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class WordCloudController {
	private final WebDBService webDb = new WebDBService();
	private final StreamProcessingService streamProcessing = new StreamProcessingService();
	private final BatchProcessingService batchProcessing = new BatchProcessingService();

	@GetMapping("/main")
	public String getMain(Model model) {
		model.addAttribute("files", this.webDb.listAllTagClouds());

		return "main";
	}

	@PostMapping("/upload")
	public String streamProcessing(@RequestParam("file") MultipartFile file, Model model) {
		try {
			model.addAttribute("message", "Datei erfolgreich hochgeladen: " + file.getOriginalFilename());
			int documentId = this.streamProcessing.process(file);
			this.webDb.createTagCloudForDocument(documentId);
			model.addAttribute("files", this.webDb.listAllTagClouds());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "Fehler beim Hochladen: " + e.getMessage());
		}

		return "main";
	}

	@GetMapping("/batch")
	public String batchProcessing(Model model) {
		try {
			// await?
			this.batchProcessing.process();
			this.webDb.createTagClouds();
			this.webDb.createGlobalTagCloud();
			model.addAttribute("message", "Batch-Job erfolgreich durchgeführt");
			model.addAttribute("files", this.webDb.listAllTagClouds());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "Fehler beim Ausführen des Batch-Jobs: " + e.getMessage());
		}

		return "main";
	}
}

class StreamProcessingService {
	public Integer process(MultipartFile _file) {
		return 0;
	}
}

class BatchProcessingService {
	public void process() {

	}
}
