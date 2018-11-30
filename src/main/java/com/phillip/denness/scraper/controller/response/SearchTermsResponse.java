package com.phillip.denness.scraper.controller.response;

import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.domain.Searchterms;

import java.util.Set;

public class SearchTermsResponse {
    private Searchterms searchterms;
    private Set<Scrape> scrapes;

    public SearchTermsResponse(Searchterms searchterms, Set<Scrape> scrapes) {
        this.searchterms = searchterms;
        this.scrapes = scrapes;
    }

    public SearchTermsResponse() {

    }

    public Searchterms getSearchterms() {
        return searchterms;
    }

    public void setSearchterms(Searchterms searchterms) {
        this.searchterms = searchterms;
    }

    public Set<Scrape> getScrapes() {
        return scrapes;
    }

    public void setScrapes(Set<Scrape> scrapes) {
        this.scrapes = scrapes;
    }
}
