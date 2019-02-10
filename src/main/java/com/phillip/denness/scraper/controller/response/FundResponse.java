package com.phillip.denness.scraper.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.phillip.denness.scraper.controller.FundController;
import com.phillip.denness.scraper.domain.Scrape;
import lombok.Getter;

import java.io.Serializable;
import java.util.Set;

@Getter
public class FundResponse implements Serializable {

    @JsonProperty("price")
    private String price;

    public FundResponse(Set<Scrape> scrapes) {
        this.price = scrapes.stream()
                .filter(scrape -> scrape.getTag().equals(FundController.PRICE_SELECTOR))
                .map(Scrape::getText)
                .findFirst().orElse("Not found price");
    }
}
