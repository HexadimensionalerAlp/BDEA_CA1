package de.wordcloud.controller;

import de.wordcloud.database.entity.DocumentEntity;
import de.wordcloud.service.StreamProcessingService;
import de.wordcloud.service.WebService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class WordCloudController {
	private final WebService webService = new WebService();
	private final StreamProcessingService streamProcessing = new StreamProcessingService();
	private final BatchProcessingService batchProcessing = new BatchProcessingService();

	@GetMapping("/main")
	public String getMain(Model model) {
		model.addAttribute("files", this.webService.listAllTagClouds());

		return "main";
	}

	@PostMapping("/upload")
	public String streamProcessing(@RequestParam("file") MultipartFile file, Model model) {
		try {
			DocumentEntity document = this.webService.saveFile(file);

			if (document.getName() != null && !document.getName().equals("")) {
				model.addAttribute("message", "Datei erfolgreich hochgeladen: " + file.getOriginalFilename());
				this.streamProcessing.process(document);
				// this.webService.createTagCloudForDocument(document.getId());
				model.addAttribute("files", this.webService.listAllTagClouds());
			}
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
			this.webService.createTagClouds();
			this.webService.createGlobalTagCloud();
			model.addAttribute("message", "Batch-Job erfolgreich durchgeführt");
			model.addAttribute("files", this.webService.listAllTagClouds());
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "Fehler beim Ausführen des Batch-Jobs: " + e.getMessage());
		}

		return "main";
	}
}

class BatchProcessingService {
	public void process() {

	}
}
