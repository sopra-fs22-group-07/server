package ch.uzh.ifi.hase.soprafs22.controller;

import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs22.repository.WhiteCardRepository;

@RestController
public class WhiteCardController {

    private final WhiteCardRepository whiteCardRepository;

    WhiteCardController(WhiteCardRepository whiteCardRepository) {
        this.whiteCardRepository = whiteCardRepository;
    }
}
