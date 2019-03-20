package com.phillip.denness.scraper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("authentication")
@Getter
@Setter
public class AuthenticationProperties {

    private long token_expiration;
    private String signing_key;
}
