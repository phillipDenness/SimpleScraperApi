package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.config.SeleniumProperties;
import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.domain.Tag;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class SeleniumWebCrawler {

    private final Logger LOGGER = LoggerFactory.getLogger(SeleniumWebCrawler.class);

    private WebDriver driver;
    private Integer repeat = 0;
    private Integer iterationsBeforeRestart;
    private IBrowser browser;

    public SeleniumWebCrawler(SeleniumProperties seleniumProperties, IBrowser browser) {
        this.browser = browser;
        iterationsBeforeRestart = seleniumProperties.getIterationsBeforeRestart();
        newBrowser();
    }

    public void newBrowser() {
        driver = browser.getDriver();
    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException {
        iterateRepeat();

        driver.get(searchterms.getDomain());

        return searchterms.getTags().stream()
                .map(String::toString)
                .map(this::getWebElement)
                .collect(Collectors.toSet());
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

    private void iterateRepeat() {
        repeat++;
        if (repeat > iterationsBeforeRestart) {
            LOGGER.info("Resetting chrome after {} iterations", repeat);
            repeat = 0;

//            driver.close();
//            driver.quit();
//            driver = null;
            newBrowser();
        }
    }

}