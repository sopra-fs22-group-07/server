package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.Play;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to
 * the game
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GameService {

  public List<BlackCard> getCards() {
    /*
    This is a helper to get black cards
    TODO: delete and replace this, as soon as the jpa black cards gets populated
     */
    ArrayList<BlackCard> cards = new ArrayList<>();
    // add 3 Cards
    BlackCard bc1 = new BlackCard();
    bc1.setId(1L);
    bc1.setText("Card 1");
    BlackCard bc2 = new BlackCard();
    bc2.setId(2L);
    bc2.setText("Card 2");
    BlackCard bc3 = new BlackCard();
    bc3.setId(3L);
    bc3.setText("Card 3");
    cards.add(bc1);
    cards.add(bc2);
    cards.add(bc3);
    return cards;
  }

    public Play getRandomPlay(Long gameId) {

      //TODO
        Play play = new Play();
      return play;
    }
}
