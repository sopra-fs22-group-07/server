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
import java.sql.Timestamp;
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
      List<Long> blackCardIds= blackCardRepository.getAllIds();
      List<BlackCard> blackCards = new ArrayList<>();
      for(int n = 0; n<numOfCards; n++){
          int randomInt = rand.nextInt(blackCardIds.size());
          long cardId = blackCardIds.get(randomInt);
          blackCards.add(getBlackCardById(cardId));
      }
      Collections.shuffle(blackCards);
      return blackCards;
  }

    /**
     * @return the oldest Game
     */
    public Game getGame(List<Game> games) {
        if (games.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no active game or past game with plays left");
        }
        return games.get(0);
    }

  /**
   * Give the game by ID
   * @param gameId: long
   * @return Game: game with unique gameId
   * @throws ResponseStatusException 404 if game does not exist
   */
    public Game getGameById(Long gameId) {
        Optional<Game> game = gameRepository.findById(gameId);
        if(game.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "game does not exist");
        }
        return game.get();
    }

    /**
     * Create a Game
     * @param blackCard Black card of the Game
     * @param user userId from user who creates the game
     * @return the created game
     */
    public Game createGame(BlackCard blackCard, User user) {
      // create new game with certain blackCard
      Game game = new Game();
      game.setBlackCard(blackCard);
      game.setUser(user);
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
    List<Long> whiteCardIds= whiteCardRepository.getAllIds();
    List<WhiteCard> whiteCards = new ArrayList<>();
    for(int n = 0; n<numOfCards; n++){
        int randomInt = rand.nextInt(whiteCardIds.size());
        long cardId = whiteCardIds.get(randomInt);
        whiteCards.add(getWhiteCardById(cardId));
    }
    Collections.shuffle(whiteCards);
    return whiteCards;
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
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "black card with cardId " + cardId + " does not exist");
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
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "white card with cardId " + cardId + " does not exist");
    }
    return card;
  }

    /**
     * Calculates the Dates for Age preferences
     * @param preference : in of age
     * @return the ages in date form
     */
  private Date calculateAgePreferencesToDate(int preference){
      Date today = new Date();

      // Convert Date to Calendar
      Calendar c = Calendar.getInstance();
      c.setTime(today);
      c.add(Calendar.YEAR, -preference);
      return c.getTime();
  }

  /**
   * Gets a game from a random user, but not the game from the user calling himself, and neither a game that that user
   * already has played on
   * @param user: user of the caller
   * @return game: a random Game.
   * @throws ResponseStatusException - 404: if there is no game of another user left
   */
  public Game getGameFromRandomUser(User user) {

    // get timestamps
    Timestamp minAgeTimestamp = new Timestamp(calculateAgePreferencesToDate(user.getMinAge()).getTime());
    Timestamp maxAgeTimestamp = new Timestamp(calculateAgePreferencesToDate(user.getMaxAge()+1).getTime());

    // count the possible games, these are all the games that match the user preferences, except for the location.
    Long numOfGames = gameRepository.countOtherUserWithActiveGameThatWasNotPlayedOn(user.getId(), user.getGender().name(), minAgeTimestamp, maxAgeTimestamp);

    // call recursive function that gets the game or throws an error
    return getGameFromRandomUserHelper(0, Math.toIntExact(numOfGames), user, minAgeTimestamp, maxAgeTimestamp);

  }

  private Game getGameFromRandomUserHelper(int page, int numOfGames, User user, Timestamp minAgeTimestamp, Timestamp maxAgeTimestamp) {
    // base: if there are zero (0) games from the count, or <=  0 from recursion, there are no games left to check
    if (numOfGames <= 0) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no black card of another user left");
    }

    // pageSize = how many games per page
    int pageSize = 100;
    // all games it found
    PageRequest pageRequest = PageRequest.of(page, pageSize);

    // get the page with the games
    Page<Game> somePage = gameRepository.getOtherUserWithActiveGameThatWasNotPlayedOn(pageRequest, user.getId(), user.getGender().name(), minAgeTimestamp, maxAgeTimestamp);

    // subset the page of users to only users that have a haversine distance of less than user.getMaxRange()
    List<Game> games = somePage.getContent();

    for (Game game : games) {
      User gameUser = game.getUser();
      if (haversineDistance(
              user.getLatitude(),
              user.getLongitude(),
              gameUser.getLatitude(),
              gameUser.getLongitude()
      ) <= user.getMaxRange()) {
        return game;
      }
    }

    // recurse
    return getGameFromRandomUserHelper(page+1, numOfGames-pageSize, user, minAgeTimestamp, maxAgeTimestamp);

  }


  /**
   * based on: https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude but adapted
   * 
   * Calculate distance between two points in latitude and longitude taking. 
   * Uses Haversine method as its base.
   * 
   * lat1, lon1 Start point; lat2, lon2 End point
   * @return: Distance in km
   */
  public static double haversineDistance(
    double lat1, 
    double lon1, 
    double lat2, 
    double lon2) {

    final int R = 6371; // Radius of the earth

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }

}