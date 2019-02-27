package com.phillip.denness.scraper.controller;

import com.phillip.denness.scraper.controller.response.FundResponse;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.webcrawler.SeleniumWebCrawler;
import org.openqa.selenium.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InvalidObjectException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
public class FundController {
    private final Logger LOGGER = LoggerFactory.getLogger(FundController.class);

    final static public String PRICE_SELECTOR = "#factsheet-tabs > fund-tabs > div > div > fund-tab.overview > div > overview > div > div.content-wrapper > div.row-separator.fund_performance.clearfix > div:nth-child(3) > div > div.columns.column-border.column-6.column-left > unit-info > div > table > tbody > tr:nth-child(1) > td:nth-child(2)";
    final static public String DIFF_SELECTOR = "#factsheet-tabs > fund-tabs > div > div > fund-tab.overview > div > overview > div > div.content-wrapper > div.row-separator.fund_performance.clearfix > div:nth-child(3) > div > div.columns.column-border.column-6.column-left > unit-info > div > table > tbody > tr:nth-child(2) > td:nth-child(2)";
    private final static String FUND_SEARCH_HREF = "#pageContent > div > fund-search > div.content-wrapper.fund-results.search-results-content > div.grid > table > tbody > tr > td.fundName > a";
    private final static String FUND_SEARCH_URL = "https://www.trustnet.com/fund/search/";

    @Autowired
    private SeleniumWebCrawler seleniumWebCrawler;

    private Set<String> tags;

    @RequestMapping(value={"trustnet"}, method=RequestMethod.GET)
    public ResponseEntity getFundPrice(@RequestParam("fund") Optional<String> fund, @RequestParam("url") Optional<String> link) {
        tags = new HashSet<>();

        String fundLink;
        if (isSearchForFund(link, fund)) {
            tags.add(FUND_SEARCH_HREF);
            fundLink = seleniumWebCrawler.doScrape(Searchterms.builder()
                    .domain(FUND_SEARCH_URL + fund.get())
                    .tags(tags).build())
                    .stream().findFirst().orElseGet(null)
                    .getHref();

            if (notFoundValidLink(fundLink)) {
                LOGGER.info("Failed to find Fund {}", FUND_SEARCH_URL + fund.get());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError("Failed to find Fund - " + FUND_SEARCH_URL + fund.get()));
            }
        } else if (link.isPresent()) {
            try {
                fundLink = URLDecoder.decode(link.get(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                LOGGER.info("Failed to decode URL {}", link.get());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError("Failed to decode URL - " + link.get()));
            }
        } else {
            LOGGER.info("Provide a url or fund");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError("Provide a url or fund"));
        }

        tags = new HashSet<>();
        tags.add(PRICE_SELECTOR);
        tags.add(DIFF_SELECTOR);

        Searchterms searchterms = Searchterms.builder()
                .domain(fundLink)
                .tags(tags).build();

        try {
            FundResponse fundResponse = new FundResponse(seleniumWebCrawler.doScrape(searchterms), fundLink);
            validateFundResponse(fundResponse);

            return ResponseEntity.status(HttpStatus.OK).body(fundResponse);
        } catch (NotFoundException e) {
            LOGGER.info("Invalid URL {}", fundLink);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError("Invalid URL - " + fundLink));
        } catch (InvalidObjectException e) {
            LOGGER.error("Fund response object has null fields ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationError("Fund response object has null fields -" + e));
        }
    }

    private boolean notFoundValidLink(String fundLink) {
        return fundLink == null;
    }

    private boolean isSearchForFund(Optional<String> link, Optional<String> fund) {
        return !link.isPresent() && fund.isPresent();
    }

    public void validateFundResponse(FundResponse fundResponse) throws InvalidObjectException {
        if (fundResponse.getFund() == null || fundResponse.getDate() == null || fundResponse.getDifference() == null || fundResponse.getPrice() == null) {
            throw new InvalidObjectException(fundResponse.toString());
        }
    }

}
