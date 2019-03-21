package com.phillip.denness.scraper.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.phillip.denness.scraper.webcrawler.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionsResponse {
    private List<Action> errors;
    private List<String> actions;
}
