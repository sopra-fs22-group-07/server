package ch.uzh.ifi.hase.soprafs22.controller;

import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs22.repository.BlackCardRepository;

@RestController
public class BlackCardController {

    private final BlackCardRepository blackCardRepository;

    BlackCardController(BlackCardRepository blackCardRepository) {
        this.blackCardRepository = blackCardRepository;
    }
}
