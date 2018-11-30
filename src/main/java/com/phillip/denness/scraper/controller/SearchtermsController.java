package com.phillip.denness.scraper.controller;

import com.phillip.denness.scraper.Service.SearchtermsService;
import com.phillip.denness.scraper.controller.response.SearchTermsResponse;
import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.webcrawler.BasicWebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/search-terms")
public class SearchtermsController {

    @Autowired
    private SearchtermsService searchTermsService;

    @Autowired
    private BasicWebCrawler basicWebCrawler;

    @GetMapping(value = "")
    public List<Searchterms> getAllSearchTerms() {
        List<Searchterms> searchTerms = searchTermsService.getAllSearchTerms();
        return searchTerms;
    }

    @PostMapping(value = "")
    public SearchTermsResponse postSearchTerms(@RequestBody Searchterms searchTermsBody) {
        Searchterms searchterms = searchTermsService.createSearchTerms(searchTermsBody);
        Set<Scrape> scrapes = basicWebCrawler.doScrape(searchterms);

        return new SearchTermsResponse(searchterms, scrapes);
    }

    @GetMapping(value = "/{id}")
    public SearchTermsResponse getSearchTermsScrapes(@PathVariable(value="id") final Integer id) {
        Optional<Searchterms> optionalSearchterms = searchTermsService.getSearchTerms(id);
        return optionalSearchterms.map(
                searchterms -> {
                    return new SearchTermsResponse(searchterms, basicWebCrawler.doScrape(searchterms));
                }
                ).orElse(new SearchTermsResponse());
    }
}
