package com.phillip.denness.scraper.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Searchterms implements Serializable {

    private String searchName;
    private String domain;
    private Set<String> tags = new HashSet<>();
    protected Set<String> keywords = new HashSet();
    protected Set<String> blockwords = new HashSet();

    public String getDomain() {
        return domain;
    }
    public Set<String> getTags() {
        return tags;
    }
    public Set<String> getKeywords() {
        return keywords;
    }
}
