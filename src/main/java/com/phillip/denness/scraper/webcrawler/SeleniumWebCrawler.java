package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.domain.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeleniumWebCrawler
{
    private final Logger LOGGER = LoggerFactory.getLogger(SeleniumWebCrawler.class);

    private WebDriver driver;

    @Autowired
    public SeleniumWebCrawler(@Value("${WEBDRIVER_PATH:/usr/bin/chromedriver}") String chromeDriverPath) throws MalformedURLException {

        LOGGER.info("Starting Selenium, Webdriver path: {}", chromeDriverPath);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        DesiredCapabilities dcap = DesiredCapabilities.chrome();
        URL gamelan = new URL("http://localhost:4444/wd/hub");
        driver = new RemoteWebDriver(gamelan, dcap);
    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException {

        driver.get(searchterms.getDomain());

        return searchterms.getTags().stream()
                .map(String::toString)
                .map(this::getWebElement)
                .collect(Collectors.toSet());
    }

    private Scrape getWebElement(String selector) {
        try {
            WebElement webElement = waitForPageLoaded(selector);
            LOGGER.info(webElement.getText());
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
}