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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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

    private DesiredCapabilities dcap;
    private URL remoteSelenium;

    @Autowired
    public SeleniumWebCrawler(@Value("${CHROME_DRIVER_PATH:/usr/bin/chromedriver}") String chromeDriverPath,
                              @Value("${SELENIUM_URL:localhost}") String seleniumUrl,
                              @Value("${SELENIUM_PORT:4444}") String seleniumPort) throws MalformedURLException {

        String remoteUrl = "http://" + seleniumUrl + ":" + seleniumPort + "/wd/hub";
        LOGGER.info("Starting Selenium, Webdriver path: {}, remote selenium: {}", chromeDriverPath, remoteUrl);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        dcap = DesiredCapabilities.chrome();
        remoteSelenium = new URL(remoteUrl);
    }

    public Set<Scrape> doScrape(Searchterms searchterms) throws NotFoundException {
        driver = new RemoteWebDriver(remoteSelenium, dcap);
        driver.get(searchterms.getDomain());

        Set<Scrape> scrapes = searchterms.getTags().stream()
                .map(String::toString)
                .map(this::getWebElement)
                .collect(Collectors.toSet());


        driver.close();

        driver.quit();
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
            driver.close();

            driver.quit();
            LOGGER.warn("Could not find selector {} ", selector);
            return Scrape.builder().tag(selector).text(null).href(null).build();
        }
    }

    private WebElement waitForPageLoaded(String selector) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(selector)));
    }
}