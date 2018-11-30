package com.phillip.denness.scraper.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "searchterms")
public class Searchterms implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "search_terms_id")
    private Integer searchTermsId;

    @Column(name = "search_name")
    private String searchName;

    @Column(name = "domain")
    private String domain;

    @ElementCollection
    @CollectionTable(
            name="searchterms_tags",
            joinColumns=@JoinColumn(name = "searchterms_search_terms_id", referencedColumnName = "search_terms_id")
    )
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name="searchterms_keywords",
            joinColumns=@JoinColumn(name = "searchterms_search_terms_id", referencedColumnName = "search_terms_id")
    )
    @Column(name = "keywords")
    protected Set<String> keywords = new HashSet();

    @ElementCollection
    @CollectionTable(
            name="searchterms_blockwords",
            joinColumns=@JoinColumn(name = "searchterms_search_terms_id", referencedColumnName = "search_terms_id")
    )
    @Column(name = "blockwords")
    protected Set<String> blockwords = new HashSet();

    public String getDomain() {
        return domain;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getSearchTermsId() {
        return searchTermsId;
    }

    public void setSearchTermsId(Integer searchTermsId) {
        this.searchTermsId = searchTermsId;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public Set<String> getBlockwords() {
        return blockwords;
    }

    public void setBlockwords(Set<String> blockwords) {
        this.blockwords = blockwords;
    }
}
