package com.phillip.denness.scraper.webcrawler;

import com.phillip.denness.scraper.config.SeleniumProperties;
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
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeleniumWebCrawler {
    private final Logger LOGGER = LoggerFactory.getLogger(SeleniumWebCrawler.class);

    private WebDriver driver;
    private DesiredCapabilities dcap;
    private URL remoteSelenium;
    private static Integer timeout;

    @Autowired
    public SeleniumWebCrawler(SeleniumProperties seleniumProperties) throws MalformedURLException {
        SeleniumWebCrawler.timeout = seleniumProperties.getTimeout_seconds();
        String remoteUrl = "http://" + seleniumProperties.getSelenium_url() + ":" + seleniumProperties.getSelenium_port() + "/wd/hub";
        LOGGER.info("Starting Selenium, Webdriver path: {}, remote selenium: {}", seleniumProperties.getChrome_driver_path(), remoteUrl);
        System.setProperty("webdriver.chrome.driver", seleniumProperties.getChrome_driver_path());
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

        closeDriver();
        return scrapes;
    }

    private Scrape getWebElement(String selector) {
        try {
            WebElement webElement = safeGetWebElement(By.cssSelector(selector), driver);
            return Scrape.builder().tag(selector)
                    .href(webElement.getAttribute(Tag.href.toString()))
                    .text(webElement.getText().trim())
                    .build();
        } catch (Throwable e) {
            closeDriver();
            LOGGER.warn("Could not find selector {} ", selector);
            return Scrape.builder().tag(selector).text(null).href(null).build();
        }
    }

    private void closeDriver() {
        driver.close();
        driver.quit();
    }

    public static WebElement safeGetWebElement(By selector, WebDriver driver, Integer seconds) {
        WebDriverWait wait = new WebDriverWait(driver, seconds);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(selector));
    }

    public static WebElement safeGetWebElement(By selector, WebDriver driver) {
        return safeGetWebElement(selector, driver, SeleniumWebCrawler.timeout);
    }

}