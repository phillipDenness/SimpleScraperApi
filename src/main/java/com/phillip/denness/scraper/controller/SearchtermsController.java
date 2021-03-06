package com.phillip.denness.scraper.controller;

import com.phillip.denness.scraper.controller.response.SearchTermsResponse;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.webcrawler.SeleniumWebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/search-terms")
public class SearchtermsController {

    @Autowired
    private SeleniumWebCrawler seleniumWebCrawler;

    @PostMapping(value = "")
    public ResponseEntity postSearchTerms(@Valid @RequestBody Searchterms searchterms) {
        return ResponseEntity.status(HttpStatus.OK).body(SearchTermsResponse.builder()
                .searchterms(searchterms)
                .scrapes(seleniumWebCrawler.doScrape(searchterms))
                .build());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationError handleException(MethodArgumentNotValidException exception) {
        return createValidationError(exception);
    }

    private ValidationError createValidationError(MethodArgumentNotValidException e) {
        return ValidationErrorBuilder.fromBindingErrors(e.getBindingResult());
    }
}
