package com.phillip.denness.scraper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.controller")
@Getter
@Setter
public class ContollerProperties {
    private Integer timeout;
}
