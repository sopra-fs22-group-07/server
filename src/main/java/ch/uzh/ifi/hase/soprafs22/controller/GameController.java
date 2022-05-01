package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to
 * the game.
 * The controller will receive the request and delegate the execution to the
 * GameService and UserService and finally return the result.
 */
@RestController
public class GameController {

  private final GameService gameService;
  private final UserService userService;
  private static final int NUM_OF_WHITE_CARDS_PER_DAY = 12;
  private static final int NUM_OF_BLACK_CARDS_TO_CHOOSE_FROM = 8;


  GameController(GameService gameService, UserService userService) {
    this.gameService = gameService;
    this.userService = userService;
  }


  @GetMapping("users/{userId}/games")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<CardGetDTO> getBlackCards(@RequestHeader(value = "authorization", required = false) String token,
                                      @PathVariable(value = "userId") Long id) {

    userService.checkSpecificAccess(token, id); // throws 401, 404

    List<BlackCard> cards;

    // We don't want the user just to refresh the page, so we only give him new black cards every 24 hours
    // In order to do that, first get current black cards, if there are any (in this case get empty list)
    cards = userService.getCurrentBlackCards(id);
    // if user has no current black cards, we get 8 new ones and assign them to the user for the next 24 hours
    if (cards == null || cards.isEmpty()){
      cards = gameService.getNRandomBlackCards(NUM_OF_BLACK_CARDS_TO_CHOOSE_FROM);
      userService.assignBlackCardsToUser(id, cards);
    }

    // return the black cards
    List<CardGetDTO> blackCardGetDTOS= new ArrayList<>();
    for (BlackCard card : cards){
      blackCardGetDTOS.add(DTOMapper.INSTANCE.convertEntityToCardGetDTO(card));
    }
    return blackCardGetDTOS;
  }

  @PostMapping("users/{userId}/games")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void createGame(@RequestHeader(value = "authorization", required = false) String token,
                                    @PathVariable(value = "userId") Long id,
                                    @RequestBody CardPostDTO blackCardPostDTO) {

    userService.checkSpecificAccess(token, id); // throws 401, 404

    // first, check if active game is older than 24 hours.
    userService.updateActiveGameIfNecessary(id);

    // only allow for creating a new game, if the user has no active game.
    if(Boolean.TRUE.equals(userService.userHasNoActiveGame(id))) {

      // get the black card from the request body
      BlackCard userInputCard = DTOMapper.INSTANCE.convertGamePostDTOToEntity(blackCardPostDTO);
      BlackCard blackCard = gameService.getBlackCardById(userInputCard.getId()); // 404

      // make sure that black card is in user's current black cards (which the user gets assigned when he retrieves some blackCards)
      userService.checkBlackCard(id, blackCard); // 403

      // create game with game service
      Game game = gameService.createGame(blackCard, id);

      // add game to user
      userService.addGame(id, game);

      // get and assign white card to the user for the next 24 hours
      // the white cards are decoupled from any time, but we only assign and create them here, so the user can play
      // white cards after his own game expired.
      List<WhiteCard> whiteCards = gameService.getNRandomWhiteCards(NUM_OF_WHITE_CARDS_PER_DAY);
      userService.assignWhiteCards(id, whiteCards);
    }
  }

    @GetMapping("users/{userId}/games/blackCards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CardAndGameIdGetDTO getBlackCardFromRandomUser(@RequestHeader(value = "authorization", required = false) String token,
                                      @PathVariable(value = "userId") Long id) {

      userService.checkSpecificAccess(token, id);

      // Get 'random' game
      Game game = gameService.getGameFromRandomUser(id);

      // return it
      CardAndGameIdGetDTO cardAndGameIdGetDTO = new CardAndGameIdGetDTO();

      cardAndGameIdGetDTO.setBlackCard(game.getBlackCard());
      cardAndGameIdGetDTO.setGameId(game.getId());

      return cardAndGameIdGetDTO;
    }

    /**
    * User gets his white Cards
     */
    @GetMapping("users/{userId}/games/whiteCards")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<CardGetDTO> getWhiteCardsFromUser(@RequestHeader(value = "authorization", required = false) String token,
                                                @PathVariable(value = "userId") Long id) {
      // check token
      userService.checkSpecificAccess(token, id); // throws 401, 404

      // get white cards of user
      List <WhiteCard> cards = userService.getWhiteCards(id);

      // return them
      List<CardGetDTO> cardGetDTO= new ArrayList<>();
      for (WhiteCard card : cards){
          cardGetDTO.add(DTOMapper.INSTANCE.convertEntityToCardGetDTO(card));
      }
      return cardGetDTO;
    }

    /**
     * get a game with all plays
     */
    @GetMapping("/users/{userId}/games/vote")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@RequestHeader(value = "authorization", required = false) String token,
                        @PathVariable(value = "userId") Long id) {

      userService.checkSpecificAccess(token, id); // throws 401, 404

      User user = userService.getUserById(id);
      // get one game (first get the old ones)
      Game game = gameService.getGame(user.getActiveGame(), user.getPastGames());
      return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    /**
     * set Like to WhiteCard
     */
    @PutMapping("/users/{userId}/games/{gameId}/vote")
    @ResponseBody
    public ResponseEntity<UserGetDTO> voteCard(
            @RequestHeader(value = "authorization", required = false) String token,
            @PathVariable(value = "gameId") Long gameId,
            @PathVariable(value = "userId") Long id,
            @RequestBody GameVotePutDTO gameVotePutDTO
                        ) {

      userService.checkSpecificAccess(token, id); // throws 401, 404

      // extract data from PutDTO
      long otherUserId = gameVotePutDTO.getUserId();
      boolean userLikesOtherUser = gameVotePutDTO.isLike();
      // TODO: 20.04.2022 Assert that this value was actually passed - maybe with javax.validation?

      // the relationship from the call is: user likes otherUser (user is the caller of the URI)
      // get both users
      User user = userService.getUserById(id);
      Game game = gameService.getGameById(gameId); // 404
      User otherUser = userService.getUserById(otherUserId); // 404

      // make sure that user does not like himself (safety net)
      if(user == otherUser) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong use of API call: you cannot vote on your cards");
      }

      // make sure that the game with gameId belongs the user who is voting
      if(!userService.isGameBelongingToUser(game, user)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong use of API call: gameId must belong to userId");
      }

      // delete play
      game = gameService.deletePlay(game, otherUserId);
      // a game can be empty, thus we delete it (if not active)
      userService.deleteGameIfEmpty(user, game);
      // Create a match between the users if they like each other
      if(userService.otherUserLikesUser(user, otherUser) && userLikesOtherUser){
        Match match = userService.createMatch(user, otherUser);
        userService.setMatch(match);
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(otherUser);
        return new ResponseEntity<>(userGetDTO, null, HttpStatus.CREATED);
      } else {
        // only set like if we haven't a match - handles multiple identical requests
        if (!userService.doesMatchExist(user, otherUser)){
          userService.setUserLikesOtherUser(user, otherUser);
        }
        return new ResponseEntity<>(null, null, HttpStatus.NO_CONTENT);
      }
    }

    /**
     * create Play of a player
     */
    @PostMapping("/users/{userId}/whiteCards/{cardId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void submitWhiteCard(@RequestHeader(value = "authorization", required = false) String token,
                            @PathVariable(value = "userId") Long id,
                            @PathVariable(value = "cardId") Long cardId,
                            @RequestBody GameIDPostDTO gameIDPostDTO){

      userService.checkSpecificAccess(token, id); // throws 401, 404

      // get the game that is voted on
      Game inputGame = DTOMapper.INSTANCE.convertGameIDPostDTOToEntity(gameIDPostDTO);
      Game game = gameService.getGameById(inputGame.getId()); // 404

      // make sure that the game does not belong to the caller himself, you shall not give a white card to your own game.
      if(userService.isGameBelongingToUser(game, userService.getUserById(id))) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Wrong use of API call: gameId must not belong to userId");
      }
      // get the played white card
      WhiteCard whiteCard = gameService.getWhiteCardById(cardId);
      // check if the white card is one that has been assigned to user and not a random one
      if(!userService.isWhiteCardBelongingToUser(whiteCard, id)){
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Wrong use of API call: You can only submit your white cards. Call \n" +
                        "GET /users/" + id + "games/whiteCards \n to get your cards!");
      }
      // create a play
      Play play = gameService.createPlay(id, cardId);
      // only assign the play to the game if user has not already played on that game (he won't lose his white card if he has)
      if(!userService.hasUserAlreadyPlayInGame(game, play)){
        gameService.putPlayInGame(game, play);
        userService.deleteWhiteCard(id, whiteCard);
      }
    }

  /**
   * Gets the current black card of a user or throws status exception
   * @param token: token of the user
   * @param userId: userId of the user
   * @return: CardGetDTO
   */
    @GetMapping("/users/{userId}/games/blackCards/current")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CardGetDTO getCurrentBlackCard(@RequestHeader(value = "authorization", required = false) String token,
                                    @PathVariable(value = "userId") Long userId) {
      userService.checkSpecificAccess(token, userId);
      // first, check if active game is older than 24 hours.
      userService.updateActiveGameIfNecessary(userId);

      BlackCard blackCard = userService.getCurrentBlackCard(userId);
      return DTOMapper.INSTANCE.convertEntityToCardGetDTO(blackCard);
    }
}
