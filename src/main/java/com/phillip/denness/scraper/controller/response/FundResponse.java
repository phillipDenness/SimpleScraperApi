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

    @JsonProperty("date")
    private String date;

    @JsonProperty("price")
    private String price;

    @JsonProperty("difference")
    private String difference;

    public FundResponse(Set<Scrape> scrapes, String fund) {
        this.fund = fund;
        mapFromScrapes(scrapes);
    }

    private void mapFromScrapes(Set<Scrape> scrapes) {
        extractDateFromText(scrapes.stream()
                .filter(scrape -> scrape.getTag().equals(FundController.PRICE_SELECTOR))
                .map(Scrape::getText)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null));

        this.difference = scrapes.stream()
                .filter(scrape -> scrape.getTag().equals(FundController.DIFF_SELECTOR))
                .map(Scrape::getText)
                .filter(Objects::nonNull)
                .findFirst().orElse("Not found difference");
    }

    private void extractDateFromText(String text) {
        System.out.println(text);
        if (text != null) {
            String[] texts = text.split("p");
            this.price = texts[0];
            int dateIndex = texts.length - 1;
            this.date = text.split("p")[dateIndex].replace("(", "").replace(")", "");
        }
    }
}
