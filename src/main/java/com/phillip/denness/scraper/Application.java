package com.phillip.denness.scraper;

import com.phillip.denness.scraper.config.ContollerProperties;
import com.phillip.denness.scraper.config.SeleniumProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SeleniumProperties.class, ContollerProperties.class})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
