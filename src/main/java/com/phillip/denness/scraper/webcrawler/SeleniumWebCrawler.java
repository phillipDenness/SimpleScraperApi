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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeleniumWebCrawler
{
    private WebDriver driver;

    @Autowired
    public SeleniumWebCrawler(@Value("${WEBDRIVER_PATH:/usr/bin/chromedriver}") String chromeDriverPath) {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        driver = new ChromeDriver(chromeOptions);
    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException{
        Set<Scrape> scrapes = new HashSet<>();

        Thread t = new Thread(new Runnable() {
            public void run()
            {
                driver.get(Thread.currentThread().getName());
            }
        }, searchterms.getDomain());
        t.start();
        try {
            t.join(20000);
        } catch (InterruptedException e) {}
        if (t.isAlive()) { // Thread still alive, we need to abort
            System.out.println("Timeout on loading page " + searchterms.getDomain());
            t.interrupt();
            return scrapes;
        }

        scrapes = searchterms.getTags().stream()
                .map(String::toString)
                .map(s -> getWebElement(s))
                .collect(Collectors.toSet());

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
            return Scrape.builder().tag(selector).text(null).href(null).build();
        }
    }

    private WebElement waitForPageLoaded(String selector) {
        By addItem = By.cssSelector(selector);
        return driver.findElement(addItem);
    }
}