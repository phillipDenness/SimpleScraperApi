package com.phillip.denness.scraper.controller;

import com.phillip.denness.scraper.controller.response.FundResponse;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.webcrawler.SeleniumWebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class FundController {
    final static public String PRICE_SELECTOR = "#factsheet-tabs > fund-tabs > div > div > fund-tab.overview > div > overview > div > div.content-wrapper > div.row-separator.fund_performance.clearfix > div:nth-child(3) > div > div.columns.column-border.column-6.column-left > unit-info > div > table > tbody > tr:nth-child(1) > td:nth-child(2)";
    final static public String DIFF_SELECTOR = "#factsheet-tabs > fund-tabs > div > div > fund-tab.overview > div > overview > div > div.content-wrapper > div.row-separator.fund_performance.clearfix > div:nth-child(3) > div > div.columns.column-border.column-6.column-left > unit-info > div > table > tbody > tr:nth-child(2) > td:nth-child(2)";
    final static public String FUND_SEARCH_HREF = "#pageContent > div > fund-search > div.content-wrapper.fund-results.search-results-content > div.grid > table > tbody > tr > td.fundName > a";

    @Autowired
    private SeleniumWebCrawler seleniumWebCrawler;

    private Set<String> tags = new HashSet<>();

    @RequestMapping(value={"trustnet"}, method= RequestMethod.GET)
    public ResponseEntity getFundPrice(@RequestParam("fund") String fund) {

        tags.add(FUND_SEARCH_HREF);

        String link = seleniumWebCrawler.doScrape(Searchterms.builder()
                .domain("https://www.trustnet.com/fund/search/" + fund)
                .tags(tags).build()).stream()
                .findFirst().orElseGet(null)
                .getHref();

        if (link == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fund not found - " + "https://www.trustnet.com/fund/search/" + fund);
        }

        tags = new HashSet<>();
        tags.add(PRICE_SELECTOR);
        tags.add(DIFF_SELECTOR);

        Searchterms searchterms = Searchterms.builder()
                .domain(link)
                .tags(tags).build();
        return ResponseEntity.status(HttpStatus.OK).body(new FundResponse(seleniumWebCrawler.doScrape(searchterms), fund));
    }
}
