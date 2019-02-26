package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.domain.Tag;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeleniumWebCrawler
{
    private WebDriver driver;

    @Autowired
    public SeleniumWebCrawler() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(chromeOptions);
    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException{
        driver.get(searchterms.getDomain());
        Set<Scrape> scrapes = searchterms.getTags().stream()
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
        WebDriverWait wait = new WebDriverWait(driver, 10);
        By addItem = By.cssSelector(selector);
        return wait.until(ExpectedConditions.presenceOfElementLocated(addItem));
    }
}