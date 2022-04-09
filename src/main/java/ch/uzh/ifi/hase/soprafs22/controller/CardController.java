package ch.uzh.ifi.hase.soprafs22.controller;

import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs22.repository.CardRepository;

@RestController
public class CardController {

    private final CardRepository CardRepository;

    CardController(CardRepository CardRepository) {
        this.CardRepository = CardRepository;
    }
}
