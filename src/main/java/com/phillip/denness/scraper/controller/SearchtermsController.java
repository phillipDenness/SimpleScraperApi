package com.phillip.denness.scraper.controller;

import com.phillip.denness.scraper.Service.SearchtermsService;
import com.phillip.denness.scraper.controller.response.SearchTermsResponse;
import com.phillip.denness.scraper.domain.Searchterms;
import com.phillip.denness.scraper.webcrawler.SeleniumWebCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/search-terms")
public class SearchtermsController {

    @Autowired
    private SearchtermsService searchTermsService;

    @Autowired
    private SeleniumWebCrawler seleniumWebCrawler;

    @GetMapping(value = "")
    public List<Searchterms> getAllSearchTerms() {
        List<Searchterms> searchTerms = searchTermsService.getAllSearchTerms();
        return searchTerms;
    }

    @PostMapping(value = "")
    public SearchTermsResponse postSearchTerms(@Valid @RequestBody Searchterms searchTermsBody) {
        Searchterms searchterms = searchTermsService.createSearchTerms(searchTermsBody);
        return new SearchTermsResponse(searchterms, seleniumWebCrawler.doScrape(searchterms));
    }

    @GetMapping(value = "/{id}")
    public SearchTermsResponse getSearchTermsScrapes(@PathVariable(value="id") final Integer id) {
        Optional<Searchterms> optionalSearchterms = searchTermsService.getSearchTerms(id);
        return optionalSearchterms.map(
                searchterms -> new SearchTermsResponse(searchterms, seleniumWebCrawler.doScrape(searchterms))
                ).orElse(new SearchTermsResponse());
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
