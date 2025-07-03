package com.khang.webextractor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class WebContentExtractorApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebContentExtractorApplication.class, args);
	}

}
