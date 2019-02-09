package com.phillip.denness.scraper.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
public class Scrape implements Serializable {

    private String text;
    private String tag;

    @Override
    public String toString() {
        return "Scrape{" +
                ", text='" + text + '\'' +
                ", tag=" + tag +
                '}';
    }
}
