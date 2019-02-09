package com.phillip.denness.scraper.domain;

import lombok.Builder;

import java.io.Serializable;

@Builder
public class Scrape implements Serializable {

    private String text;
    private String tag;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Scrape{" +
                ", text='" + text + '\'' +
                ", tag=" + tag +
                '}';
    }
}
