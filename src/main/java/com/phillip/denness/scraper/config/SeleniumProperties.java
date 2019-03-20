package com.phillip.denness.scraper.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@ConfigurationProperties("selenium")
@Getter
@Setter
public class SeleniumProperties {

    private String aviva_credentials;
    private Map<String, String> credentialStore;

    @PostConstruct
    public void init() {
        String[] splitCredentials = aviva_credentials.split(" ");

        credentialStore = new HashMap<>();
        for (String splitCredential : splitCredentials) {
            String[] userPw = splitCredential.split(",");
            if (userPw.length == 2) {
                Optional<String> user = Optional.ofNullable(userPw[0]);
                Optional<String> pw = Optional.ofNullable(userPw[1]);
                if (user.isPresent() && pw.isPresent()) {
                    credentialStore.put(user.get(), pw.get());
                }
            }
        }
    }

    public Optional<String> getPassword(String username) {
        return Optional.ofNullable(credentialStore.get(username));
    }
}
