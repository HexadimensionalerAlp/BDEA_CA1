package de.wordcloud.controller;

import de.wordcloud.database.entity.DocumentEntity;
import de.wordcloud.service.BatchProcessingService;
import de.wordcloud.service.StreamProcessingService;
import de.wordcloud.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class WordCloudController {
	@Autowired
	private final WebService webService;
	@Autowired
	private final StreamProcessingService streamProcessing;
	@Autowired
	private final BatchProcessingService batchProcessing;

	public WordCloudController(WebService webService, StreamProcessingService streamProcessing, BatchProcessingService batchProcessing) {
		this.webService = webService;
		this.streamProcessing = streamProcessing;
		this.batchProcessing = batchProcessing;
	}

	@GetMapping("/main")
	public String getMain(Model model) {
		model.addAttribute("files", this.webService.listAllTagClouds());

		return "main";
	}

	@PostMapping("/upload")
	public String streamProcessing(@RequestParam("file") MultipartFile file, Model model) {
		if (file != null) {
			try {
				DocumentEntity document = this.webService.saveFile(file);

				if (document.getName() != null && !document.getName().equals("")) {
					model.addAttribute("message", "Datei erfolgreich hochgeladen: " + file.getOriginalFilename());
					this.streamProcessing.process(document);
					this.webService.createTagCloudForDocument(document.getId());
					model.addAttribute("files", this.webService.listAllTagClouds());
				}
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("message", "Fehler beim Hochladen: " + e.getMessage());
			}
		} else {
			model.addAttribute("message", "Fehler beim Hochladen: Keine Datei ausgew??hlt");
		}

		return "main";
	}

	@GetMapping("/batch")
	public String batchProcessing(Model model) {
		try {
			this.batchProcessing.process();
			this.webService.createGlobalTagCloud();
			this.webService.createTagClouds();
			model.addAttribute("message", "Batch-Job erfolgreich durchgef??hrt");
			model.addAttribute("files", this.webService.listAllTagClouds());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "Fehler beim Ausf??hren des Batch-Jobs: " + e.getMessage());
		}

		return "main";
	}
}
