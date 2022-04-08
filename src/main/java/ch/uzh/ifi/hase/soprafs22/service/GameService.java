package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.Card;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GameService {

  private final Logger log = LoggerFactory.getLogger(GameService.class);

  private final UserRepository userRepository;

  @Autowired
  public GameService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  public List<Card> getCards() {
    ArrayList<Card> cards = new ArrayList<>();
    cards.add(new BlackCard(1, "Card 1"));
    cards.add(new BlackCard(2, "Card 2"));
    cards.add(new BlackCard(3, "Card 3"));
    return cards;
  }
}
