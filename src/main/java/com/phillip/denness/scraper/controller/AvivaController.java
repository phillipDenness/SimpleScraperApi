package com.phillip.denness.scraper.controller;

import com.phillip.denness.scraper.config.AvivaProps;
import com.phillip.denness.scraper.controller.response.ActionsResponse;
import com.phillip.denness.scraper.webcrawler.Action;
import com.phillip.denness.scraper.webcrawler.Aviva;
import com.phillip.denness.scraper.webcrawler.SeleniumWebCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class AvivaController {
    private final Logger LOGGER = LoggerFactory.getLogger(AvivaController.class);

    private SeleniumWebCrawler seleniumWebCrawler;

    @Autowired
    public AvivaController(SeleniumWebCrawler seleniumWebCrawler, AvivaProps avivaProps) {
        this.seleniumWebCrawler = seleniumWebCrawler;
        System.setProperty("pdenness", avivaProps.getPdenness());
    }

    @RequestMapping(value={"aviva"}, method=RequestMethod.GET)
    public ResponseEntity<ActionsResponse> runAviva(@RequestParam("username") String username,
                                                    @RequestParam("fund") String fund) {

        if (System.getProperty(username) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ActionsResponse.builder()
                    .errors(Collections.singletonList(Action.builder()
                            .error(username + " is not registered to use this service").build())
                    ).build());
        }

        HashMap<String, String> config = new HashMap<>();
        config.put("username", username);
        config.put("password", System.getProperty(username));
        config.put("fund", fund);

        Aviva aviva = new Aviva(config);

        List<Action> actions = seleniumWebCrawler.executeActions(aviva);
        return validateActions(actions);
    }


    private ResponseEntity<ActionsResponse> validateActions(List<Action> actions) {

        List<Action> errors = actions.stream()
                .filter(action -> action.getError() != null)
                .collect(Collectors.toList());

        List<String> allActions = new ArrayList<>();
        for (Action action : actions) {
            String responseLine = action.getId() + ". " + action.getStep();
            allActions.add(responseLine);
        }
        if (errors.size() > 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ActionsResponse.builder()
                    .errors(errors)
                    .actions(allActions)
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(ActionsResponse.builder()
                .actions(allActions)
                .build());
    }
}
