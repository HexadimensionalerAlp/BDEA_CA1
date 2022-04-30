package de.wordcloud.web;

import java.io.File;

import de.wordcloud.service.WebService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		File path = new File(WebService.TAG_CLOUD_PATH);
		
		if (!path.exists())
			path.mkdir();
			
		registry.addResourceHandler("/" + WebService.TAG_CLOUD_PATH + "**").addResourceLocations("file:" + WebService.TAG_CLOUD_PATH);
	}
	
}
