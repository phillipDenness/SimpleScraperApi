package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeleniumWebCrawler
{
    @Value("${WEBDRIVER_PATH}")
    private String chromeDriverPath;

    private WebDriver driver;
    final ChromeOptions chromeOptions = new ChromeOptions();

    public SeleniumWebCrawler() {
        chromeOptions.setHeadless(true);
    }

    private void checkWebDriver() {
        if (System.getProperty("webdriver.chrome.driver") == null) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        }
    }

    public Set<Scrape> doScrape(Searchterms searchterms) {

        checkWebDriver();

        driver = new ChromeDriver(chromeOptions);

        Set<Scrape> scrapes;
        waitForPageLoaded();
        driver.get(searchterms.getDomain());
        scrapes = searchterms.getTags().stream()
                .map(String::toString)
                .map(s -> getWebElement(s))
                .collect(Collectors.toSet());

        driver.close();
        driver.quit();
        return scrapes;
    }

    private Scrape getWebElement(String selector) {
        try {
            WebElement webElement = driver.findElement(By.cssSelector(selector));
            return Scrape.builder().tag(selector)
                    .href(webElement.getAttribute("href"))
                    .text(webElement.getText().trim())
                    .build();
        } catch (Throwable e) {
            return Scrape.builder().tag(selector).text(null).href(null).build();
        }
    }

    public void waitForPageLoaded() {
        try {
            Thread.sleep(5000);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}