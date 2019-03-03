package com.phillip.denness.scraper.controller;

import com.phillip.denness.scraper.config.ContollerProperties;
import com.phillip.denness.scraper.controller.response.FundResponse;
import com.phillip.denness.scraper.domain.Scrape;
import com.phillip.denness.scraper.service.ScrapeService;
import org.openqa.selenium.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.InvalidObjectException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class FundController {
    private final Logger LOGGER = LoggerFactory.getLogger(FundController.class);

    @Autowired
    private ContollerProperties properties;

    @Autowired
    private ScrapeService service;

    @GetMapping("/trustnet")
    public ResponseEntity getFundResponse(@RequestParam("url") String encodedLink) throws InterruptedException, ExecutionException {

        String link = null;
        try {
            link = URLDecoder.decode(encodedLink, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Failed to decode URL {}", encodedLink);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError("Failed to decode URL - " + encodedLink));
        }

        Future<Set<Scrape>> futureResponse = service.call(link);
        try {
            try {
                Set<Scrape> scrapes = futureResponse.get(properties.getTimeout(), TimeUnit.SECONDS);
                FundResponse fundResponse = new FundResponse(scrapes, link);
                validateFundResponse(fundResponse);

                return ResponseEntity.status(HttpStatus.OK).body(fundResponse);

            } catch (NotFoundException e) {

                LOGGER.info("Invalid URL {}", link);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ValidationError("Invalid URL - " + link));
            } catch (InvalidObjectException e) {

                LOGGER.error("Fund response object has null fields ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ValidationError("Fund response object has null fields -" + e));
            }

        } catch (TimeoutException te) {
            if (futureResponse.cancel(true) || !futureResponse.isDone()) {
                service.newBrowser();
                throw new TestTimeoutException();
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(futureResponse.get());
            }
        }
    }

    @ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT, reason = "Cancelling request due to taking too much time")
    public class TestTimeoutException extends RuntimeException { }

    public void validateFundResponse(FundResponse fundResponse) throws InvalidObjectException {
        if (fundResponse.getFund() == null || fundResponse.getDate() == null || fundResponse.getDifference() == null || fundResponse.getPrice() == null) {
            throw new InvalidObjectException(fundResponse.toString());
        }
    }

}
