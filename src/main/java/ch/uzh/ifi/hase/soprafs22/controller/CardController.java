package ch.uzh.ifi.hase.soprafs22.controller;

import org.springframework.web.bind.annotation.RestController;

import ch.uzh.ifi.hase.soprafs22.repository.BlackCardRepository;
import ch.uzh.ifi.hase.soprafs22.repository.WhiteCardRepository;

@RestController
public class CardController {

    private final BlackCardRepository blackCardRepository;
    private final WhiteCardRepository whiteCardRepository;

    CardController(BlackCardRepository BlackCardRepository, WhiteCardRepository WhiteCardRepository) {
        this.blackCardRepository = BlackCardRepository;
        this.whiteCardRepository = WhiteCardRepository;
    }
}
