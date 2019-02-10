package com.phillip.denness.scraper.controller.response;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class SearchTermsResponse {
    private Searchterms searchterms;
    private Set<Scrape> scrapes;

    public SearchTermsResponse(Searchterms searchterms, Set<Scrape> scrapes) {
        this.searchterms = searchterms;
        this.scrapes = scrapes;
    }
}
