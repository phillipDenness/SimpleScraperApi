package com.phillip.denness.scraper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.selenium")
@Getter
@Setter
public class SeleniumProperties {
    private String geckodriverPath;
    private String chromeDriverPath;
    private String chromeBinary;
    private String firefoxBinary;
    private Integer iterationsBeforeRestart;

}
