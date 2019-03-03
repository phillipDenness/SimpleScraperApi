package com.phillip.denness.scraper.service;

import com.phillip.denness.scraper.config.SeleniumProperties;
import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.webcrawler.Firefox;
import com.phillip.denness.scraper.webcrawler.SeleniumWebCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

@Service
public class ScrapeService {

    private final Logger LOGGER = LoggerFactory.getLogger(ScrapeService.class);

    final static public String PRICE_SELECTOR = "#factsheet-tabs > fund-tabs > div > div > fund-tab.overview > div > overview > div > div.content-wrapper > div.row-separator.fund_performance.clearfix > div:nth-child(3) > div > div.columns.column-border.column-6.column-left > unit-info > div > table > tbody > tr:nth-child(1) > td:nth-child(2)";
    final static public String DIFF_SELECTOR = "#factsheet-tabs > fund-tabs > div > div > fund-tab.overview > div > overview > div > div.content-wrapper > div.row-separator.fund_performance.clearfix > div:nth-child(3) > div > div.columns.column-border.column-6.column-left > unit-info > div > table > tbody > tr:nth-child(2) > td:nth-child(2)";
    private final static String FUND_SEARCH_HREF = "#pageContent > div > fund-search > div.content-wrapper.fund-results.search-results-content > div.grid > table > tbody > tr > td.fundName > a";
    private final static String FUND_SEARCH_URL = "https://www.trustnet.com/fund/search/";

    private Set<String> tags;
    private SeleniumWebCrawler seleniumWebCrawler;

    @Autowired
    public ScrapeService(SeleniumProperties seleniumProperties, Firefox browser) {
        seleniumWebCrawler = new SeleniumWebCrawler(seleniumProperties, browser);
        tags = new HashSet<>();
        tags.add(PRICE_SELECTOR);
        tags.add(DIFF_SELECTOR);
    }

    @Async("threadPoolTaskExecutor")
    public Future<Set<Scrape>> call(String url) {
        try{
            Searchterms searchterms = Searchterms.builder()
                    .domain(url)
                    .tags(tags).build();

            return new AsyncResult<>(seleniumWebCrawler.doScrape(searchterms));
        } catch (Exception e) {
            //some cancel/rollback logic when the request is cancelled
            return null;
        }
    }

    public void newBrowser() {
        seleniumWebCrawler.newBrowser();
    }
}