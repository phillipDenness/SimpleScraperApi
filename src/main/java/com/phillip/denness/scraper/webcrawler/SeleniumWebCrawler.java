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
import org.openqa.selenium.support.ui.ExpectedConditions;
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
    private final ChromeOptions chromeOptions = new ChromeOptions();

    public SeleniumWebCrawler() {
        chromeOptions.setHeadless(true);
    }

    private void checkWebDriver() {
        if (System.getProperty("webdriver.chrome.driver") == null) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        }
    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException{

        checkWebDriver();

        driver = new ChromeDriver(chromeOptions);
        Set<Scrape> scrapes;

        try {
            driver.get(searchterms.getDomain());
            scrapes = searchterms.getTags().stream()
                    .map(String::toString)
                    .map(s -> getWebElement(s))
                    .collect(Collectors.toSet());

            driver.close();
            driver.quit();

            return scrapes;
        } catch (Throwable e) {
            driver.close();
            driver.quit();
            throw new NotFoundException(e);
        }
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
        WebDriverWait wait = new WebDriverWait(driver, 30);
        By addItem = By.cssSelector(selector);
        return wait.until(ExpectedConditions.presenceOfElementLocated(addItem));
    }
}