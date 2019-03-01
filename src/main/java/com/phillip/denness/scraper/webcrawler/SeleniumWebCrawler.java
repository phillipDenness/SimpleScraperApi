package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.domain.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeleniumWebCrawler
{
    private final Logger LOGGER = LoggerFactory.getLogger(SeleniumWebCrawler.class);

    private WebDriver driver;
    private ChromeOptions chromeOptions;

    @Autowired
    public SeleniumWebCrawler(@Value("${WEBDRIVER_PATH:/usr/bin/chromedriver}") String chromeDriverPath,
                              @Value("${CHROME_BINARY:/usr/bin/headless-chromium}") String chromeBinary) {

        LOGGER.info("Starting Selenium, Webdriver path: {}, Chrome binary: {} ", chromeDriverPath, chromeBinary);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        chromeOptions = new ChromeOptions();
        chromeOptions.setBinary(chromeBinary);
        chromeOptions.addArguments("--headless"); // only if you are ACTUALLY running headless
        chromeOptions.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
        chromeOptions.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/43840128/1689770
        chromeOptions.addArguments("--single-process"); //https://stackoverflow.com/a/50725918/1689770

        driver = new ChromeDriver(chromeOptions);

    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException {
        driver.get(searchterms.getDomain());

        Set<Scrape> scrapes = searchterms.getTags().stream()
                .map(String::toString)
                .map(s -> getWebElement(s))
                .collect(Collectors.toSet());

//        iterateRepeat();
        return scrapes;
    }

    private Scrape getWebElement(String selector) {
        try {
            WebElement webElement = waitForPageLoaded(selector);
            return Scrape.builder().tag(selector)
                    .href(webElement.getAttribute(Tag.href.toString()))
                    .text(webElement.getText().trim())
                    .build();
        } catch (Throwable e) {
            LOGGER.warn("Could not find selector {} ", selector);
            return Scrape.builder().tag(selector).text(null).href(null).build();
        }
    }

    private WebElement waitForPageLoaded(String selector) {
        By addItem = By.cssSelector(selector);
        return driver.findElement(addItem);
    }

//    private void iterateRepeat() {
//        repeat++;
//        if (repeat > 3) {
//            LOGGER.info("Resetting chrome after {} iterations", repeat);
//            repeat = 0;
//            driver.close();
//            driver = new ChromeDriver(chromeOptions);
//        }
//    }

}