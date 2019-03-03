package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.Utils;
import com.phillip.denness.scraper.config.SeleniumProperties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Chrome implements IBrowser {

    private final Logger LOGGER = LoggerFactory.getLogger(Chrome.class);
    private ChromeOptions options;

    public Chrome(SeleniumProperties seleniumProperties) {
        LOGGER.info("Starting Selenium, Webdriver path: {}, binary path: {}, Iterations per browser: {}",
                seleniumProperties.getChromeDriverPath(),
                seleniumProperties.getChromeBinary(),
                seleniumProperties.getIterationsBeforeRestart());
        System.setProperty("webdriver.chrome.driver", seleniumProperties.getChromeDriverPath());

        options = new ChromeOptions();

        if (!Utils.isWindows()) {
            LOGGER.info("Linux machine, set binary and make headless");
            options.setBinary(seleniumProperties.getChromeBinary());
            options.addArguments("--single-process"); //https://stackoverflow.com/a/50725918/1689770
            options.addArguments("--headless"); // only if you are ACTUALLY running headless
        }
        options.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/43840128/1689770
    }

    public WebDriver getDriver() {
       return new ChromeDriver(options);
    }
}
