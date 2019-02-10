package com.phillip.denness.scraper.domain;

import lombok.Builder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Builder
public class Searchterms implements Serializable {

    private String searchName;
    private String domain;
    private Set<String> tags;
    protected Set<String> keywords;
    protected Set<String> blockwords;

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
