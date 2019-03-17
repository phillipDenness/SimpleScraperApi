package com.phillip.denness.scraper.webcrawler;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Action {

    private Integer id;
    private String error;
    private String step;
}
