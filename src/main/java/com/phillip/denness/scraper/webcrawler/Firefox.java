package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.Utils;
import com.phillip.denness.scraper.config.SeleniumProperties;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Firefox implements IBrowser {

    private final Logger LOGGER = LoggerFactory.getLogger(Firefox.class);
    private FirefoxOptions options;

    public Firefox(SeleniumProperties seleniumProperties) {
        LOGGER.info("Starting Selenium, Webdriver path: {}, binary path: {}, Iterations per browser: {}",
                seleniumProperties.getGeckodriverPath(),
                seleniumProperties.getFirefoxBinary(),
                seleniumProperties.getIterationsBeforeRestart());
        System.setProperty("webdriver.gecko.driver", seleniumProperties.getGeckodriverPath());

        options = new FirefoxOptions();

        if (!Utils.isWindows()) {
            LOGGER.info("Linux machine, set binary and make headless");
        }
        options.setBinary(seleniumProperties.getFirefoxBinary());
        options.setHeadless(true); // only if you are ACTUALLY running headless
        options.addArguments("--single-process"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
        options.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/43840128/1689770
    }

    public WebDriver getDriver() {
        return new FirefoxDriver(options);
    }
}
