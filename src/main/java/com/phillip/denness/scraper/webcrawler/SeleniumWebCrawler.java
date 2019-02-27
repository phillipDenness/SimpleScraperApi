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

    private Integer sessions;
    private ChromeOptions chromeOptions;

    @Autowired
    public SeleniumWebCrawler(@Value("${WEBDRIVER_PATH:/usr/bin/chromedriver}") String chromeDriverPath) {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("start-maximized"); // https://stackoverflow.com/a/26283818/1689770
        chromeOptions.addArguments("enable-automation"); // https://stackoverflow.com/a/43840128/1689770
        chromeOptions.addArguments("--headless"); // only if you are ACTUALLY running headless
        chromeOptions.addArguments("--no-sandbox"); //https://stackoverflow.com/a/50725918/1689770
        chromeOptions.addArguments("--disable-infobars"); //https://stackoverflow.com/a/43840128/1689770
        chromeOptions.addArguments("--disable-dev-shm-usage"); //https://stackoverflow.com/a/50725918/1689770
        chromeOptions.addArguments("--disable-browser-side-navigation"); //https://stackoverflow.com/a/49123152/1689770
        chromeOptions.addArguments("--disable-gpu"); //https://stackoverflow.com/questions/51959986/how-to-solve-selenium-chromedriver-timed-out-receiving-message-from-renderer-exc

        driver = new ChromeDriver(chromeOptions);
        sessions = 0;
    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException {
        Set<Scrape> scrapes = new HashSet<>();

//        manageSessions();

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

    private void manageSessions() {
        sessions += sessions;
        if (sessions > 5 ) {
            driver.quit();
            driver = new ChromeDriver(chromeOptions);
            sessions = 0;
        }
    }
}