package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
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
            System.out.println(chromeDriverPath);
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        }
    }

    public Set<Scrape> doScrape(Searchterms searchterms) {

        checkWebDriver();
        Set<Scrape> scrapes;

        driver = new ChromeDriver(chromeOptions);
        waitForPageLoaded();
        driver.get(searchterms.getDomain());
        scrapes = searchterms.getTags().stream()
                .map(String::toString)
                .map(s -> getSelectorText(s))
                .collect(Collectors.toSet());

        driver.close();
        return scrapes;
    }

    private Scrape getSelectorText(String selector) {
        String midprice = driver.findElement(By.cssSelector(selector)).getText();
        return Scrape.builder().tag(selector).text(midprice).build();
    }

    public void waitForPageLoaded() {
        ExpectedCondition<Boolean> expectation = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(expectation);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}