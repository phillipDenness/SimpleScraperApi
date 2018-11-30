package com.phillip.denness.scraper.domain;

import java.io.Serializable;

public class Scrape implements Serializable {

    private String text;
    private Tag tag;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
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
