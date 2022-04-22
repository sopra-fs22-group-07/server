package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.BlackCardRepository;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.PlayRepository;
import ch.uzh.ifi.hase.soprafs22.repository.WhiteCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
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

    private final GameRepository gameRepository;
    private final PlayRepository playRepository;
    private final WhiteCardRepository whiteCardRepository;
    private final BlackCardRepository blackCardRepository;

    private static final SecureRandom rand = new SecureRandom();

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("playRepository") PlayRepository playRepository,
                       @Qualifier("WhiteCardRepository") WhiteCardRepository whiteCardRepository,
                       @Qualifier("BlackCardRepository") BlackCardRepository blackCardRepository) {
        this.gameRepository = gameRepository;
        this.playRepository = playRepository;
        this.whiteCardRepository = whiteCardRepository;
        this.blackCardRepository = blackCardRepository;
    }

  /**
   * Fetch n random Cards from the blackCardRepository
   * @return List of Black card
   */
  // See solution from https://stackoverflow.com/a/52409343/17532411
  public List<BlackCard> getNRandomBlackCards(int numOfCards) {
    // TODO: 12.04.2022 It seems that it isn't quite random: Try it with PostMan and see that the cardIds are always near each other

    // get 8 random cards from the blackCardRepository
    int totalRecords = (int) blackCardRepository.count();

    Page<BlackCard> somePage = blackCardRepository.findAll(getPageRequest(totalRecords, numOfCards));
    List<BlackCard> cards;
    if ( somePage.getTotalElements() > 0){
      cards = new ArrayList<>(somePage.getContent());
    } else {
      cards = new ArrayList<>();
    }
    Collections.shuffle(cards);
    return cards;
  }

    /**
     * @return the oldest Game
     */
    public Game getGame(Game activeGame, List<Game> pastGames) {
        return pastGames.isEmpty() ? activeGame : pastGames.get(0);
    }

  /**
   * Give the game by ID
   * @param gameId: long
   * @return Game: game with unique gameId
   * @throws ResponseStatusException 404 if game does not exist
   */
    public Game getGameById(long gameId) {
        Game game = gameRepository.findById(gameId);
        if(game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game does not exist");
        }
        return game;
    }

    /**
     * Create a Game
     * @param blackCard Black card of the Game
     * @param userId userId from user who creates the game
     * @return the created game
     */
    public Game createGame(BlackCard blackCard, long userId) {
      // create new game with certain blackCard
      Game game = new Game();
      game.setBlackCard(blackCard);
      game.setUserId(userId);
      game.setGameStatus(GameStatus.ACTIVE);

      // save the game
      return gameRepository.saveAndFlush(game);
    }

    /**
     * Create a Play (whiteCard and the userId from the played)
     * @param userId id of user who creates play
     * @param cardId id of card to get added to the play
     * @return Play - created Play
     */
    public Play createPlay(long userId, long cardId){
      // instance of new play
      WhiteCard card = whiteCardRepository.findById(cardId);
      // set card and id
      Play play = new Play();
      play.setCard(card);
      play.setUserId(userId);

      // DO NOT SAVE AND FLUSH THE PLAY HERE (see putPlayInGame()).
      return play;
    }

  /**
   * Assuming the Play is valid, that is there is no other play from that user in that game, we put into the game
   * @param game: Game into which the Play gets added
   * @param play: Play which gets added into the Game
   */
    public void putPlayInGame(Game game, Play play) {

      // set gameId in play here for joining game and play repos
      play.setGameId(game.getId());
      game.enqueuePlay(play);
      // save and flush
      // we only saveAndFlush the play here (after it is checked)
      playRepository.saveAndFlush(play);
      gameRepository.saveAndFlush(game);
    }

  /**
   * Fetch n random Cards from the whiteCardRepository
   * @return List of White cards
   */
  // Implementation identical to getNRandomBlackCards(): https://stackoverflow.com/a/52409343/17532411
  public List<WhiteCard> getNRandomWhiteCards(int numOfCards) {
    int totalRecords = (int) whiteCardRepository.count();

    Page<WhiteCard> somePage = whiteCardRepository.findAll(getPageRequest(totalRecords, numOfCards));
    List<WhiteCard> cards;
    if ( somePage.getTotalElements() > 0){
      cards = new ArrayList<>(somePage.getContent());
    } else {
      cards = new ArrayList<>();
    }
    Collections.shuffle(cards);
    return cards;
  }

  // extract some duplicate code (from getNRandomCards) : https://stackoverflow.com/a/52409343/17532411
  public static PageRequest getPageRequest(int totalRecords, int numOfCards){
    int totalPages =
            (totalRecords % numOfCards == 0)
                    ? (totalRecords / numOfCards)
                    : ((totalRecords / numOfCards) + 1);
    int pageIndex = rand.nextInt(totalPages);
    return PageRequest.of(pageIndex, numOfCards);
  }

  /**
   * Given a Game, we want to delete the play from user with userId (We look out for multiple plays from this user,
   * even though that should not be possible from the REST calls.
   * @param game: Game in which the Play shall be deleted
   * @param userId: userId from User whose Play shall be deleted
   * @return Game: The updated Game
   */
  public Game deletePlay(Game game, Long userId) {
    game.deletePlaysFrom(userId);
    gameRepository.saveAndFlush(game);
    return game;
  }


  /**
   * Get Black Card from the CardRepository, by cardId
   * @param cardId: unique card cardId for this card
   * @return Black Card
   * @throws ResponseStatusException - 404: if there is no such card in the repository
   */
  public BlackCard getBlackCardById(long cardId) {
    BlackCard card = blackCardRepository.findById(cardId);
    if(card == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "black card with cardId " + cardId + " does not exit");
    }
    return card;
  }

  /**
   * Get White Card from the CardRepository, by cardId
   * @param cardId: unique card cardId for this card
   * @return White Card
   * @throws ResponseStatusException - 404: if there is no such card in the repository
   */
  public WhiteCard getWhiteCardById(long cardId) {
    WhiteCard card = whiteCardRepository.findById(cardId);
    if(card == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "white card with cardId " + cardId + " does not exit");
    }
    return card;
  }

  /**
   * Gets a game from a random user, but not the game from the user calling himself, and neither a game that that user
   * already has played on
   * @param userId: userId of the caller
   * @return Game: a random Game.
   */
  public Game getGameFromRandomUser(Long userId) {
    // count the possible games
    Long numOfGames = gameRepository.countOtherUserWithActiveGameThatWasNotPlayedOn(userId);
    // limit page size to 100
    int pageSize = (numOfGames < 100) ? numOfGames.intValue() : 100;
    int pageIndex = rand.nextInt(pageSize);
    // only get one game
    PageRequest pageRequest = PageRequest.of(pageIndex, 1);

    // get the page with the game
    Page<Game> somePage = gameRepository.getOtherUserWithActiveGameThatWasNotPlayedOn(pageRequest, userId);

    // return the game if there is any, else return null
    if (somePage.getTotalElements() > 0) {
      return somePage.getContent().get(0);
    }
     return null;
  }
}
