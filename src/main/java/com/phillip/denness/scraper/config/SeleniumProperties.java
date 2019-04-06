package com.phillip.denness.scraper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("selenium")
@Getter
@Setter
public class SeleniumProperties {

    private Integer timeout_seconds;
    private String chrome_driver_path;
    private String selenium_url;
    private String selenium_port;

}
