package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.CardRepository;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    private final GameRepository gameRepository;

    private final PlayRepository playRepository;

    private final CardRepository cardRepository;
    private Random rand = new Random();

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("playRepository") PlayRepository playRepository,
                       @Qualifier("cardRepository") CardRepository cardRepository) {
        this.gameRepository = gameRepository;
        this.playRepository = playRepository;
        this.cardRepository = cardRepository;
    }

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

    /**
     *  Get a random Play (white card and user) from a game
     * @param gameId id of game to search
     * @return a random play
     */
    public Play getRandomPlay(Long gameId) {
        Game game = getGameById(gameId);

        List<Play> plays = game.getPlays();

        Play randomPlay = plays.get(rand.nextInt(plays.size()));

        if (randomPlay==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "there are no cards left to be voted on");

        }
        return randomPlay;
    }

    private Game getGameById(long gameId) {
        Game game = gameRepository.findById(gameId);
        if(game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user does not exit");
        }
        return game;
    }

    /**
     * Create a Game
     * @param userInputCard Black card of the Game
     * @param userId user, which creates the game
     * @return the created game
     */
    public Game createGame(BlackCard userInputCard, long userId) {
      // create new game with certain blackCArd
      Game game = new Game();
      game.setBlackCard(userInputCard);
      game.setUserId(userId);
      gameRepository.saveAndFlush(game);
      return game;
    }

    /**
     * Create a Play (whiteCard and the userId from the played)
     * @param userId id of user, which creates play
     * @param gameId id of game, for which play gets created
     * @param cardId id of card, ehich gets added to the play
     */
    public void createPlay(long userId,long gameId, long cardId){
        Game game = gameRepository.findById(gameId);
        // instance of new play
        Play play = new Play();
        WhiteCard card = (WhiteCard) cardRepository.findById(cardId);
        // set card and id
        play.setCard(card);
        play.setUserId(userId);
        game.enqueuePlay(play);
        // save and flush
        gameRepository.saveAndFlush(game);
        playRepository.saveAndFlush(play);
    }

    public void setPlayLike(Long gameId) {
        //TODO

    }
}
