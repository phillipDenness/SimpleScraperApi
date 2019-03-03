package com.phillip.denness.scraper.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search-terms")
public class SearchtermsController {
//
//    @Autowired
//    private SeleniumWebCrawler seleniumWebCrawler;
//
//    @PostMapping(value = "")
//    public ResponseEntity postSearchTerms(@Valid @RequestBody Searchterms searchterms) {
//        return ResponseEntity.status(HttpStatus.OK).body(SearchTermsResponse.builder()
//                .searchterms(searchterms)
//                .scrapes(seleniumWebCrawler.doScrape(searchterms))
//                .build());
//    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ValidationError handleException(MethodArgumentNotValidException exception) {
        return createValidationError(exception);
    }

    private ValidationError createValidationError(MethodArgumentNotValidException e) {
        return ValidationErrorBuilder.fromBindingErrors(e.getBindingResult());
    }
}
