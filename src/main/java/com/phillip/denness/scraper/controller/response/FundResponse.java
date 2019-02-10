package com.phillip.denness.scraper.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.phillip.denness.scraper.controller.FundController;
import com.phillip.denness.scraper.domain.Scrape;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Getter
public class FundResponse implements Serializable {

    @JsonProperty("fund")
    private String fund;

    @JsonProperty("price")
    private String price;

    @JsonProperty("difference")
    private String difference;

    public FundResponse(Set<Scrape> scrapes, String fund) {
        this.fund = fund;
        this.price = scrapes.stream()
                .filter(scrape -> scrape.getTag().equals(FundController.PRICE_SELECTOR))
                .map(Scrape::getText)
                .filter(Objects::nonNull)
                .findFirst().orElse("Not found price");

        this.difference = scrapes.stream()
                .filter(scrape -> scrape.getTag().equals(FundController.DIFF_SELECTOR))
                .map(Scrape::getText)
                .filter(Objects::nonNull)
                .findFirst().orElse("Not found difference");
    }
}
