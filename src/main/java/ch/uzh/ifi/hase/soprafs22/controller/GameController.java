package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to
 * the game.
 * The controller will receive the request and delegate the execution to the
 * GameService and finally return the result.
 */
@RestController
public class GameController {

  private final GameService gameService;
  private final UserService userService;

  GameController(GameService gameService, UserService userService) {
    this.gameService = gameService;
    this.userService = userService;
  }

  @GetMapping("users/{userId}/games")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<CardGetDTO> getBlackCard(@RequestHeader(value = "authorization", required = false) String token,
                                      @PathVariable(value = "userId") Long id) {

    // check if source of query has access token
    userService.checkSpecificAccess(token, id); // throws 401, 404
    List<BlackCard> cards = gameService.getCards();
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

    BlackCard userInputCard = DTOMapper.INSTANCE.convertGamePostDTOToEntity(blackCardPostDTO);

    //TODO: check if time is up

    // if time is up, create game with game service
    Game game = gameService.createGame(userInputCard, id);
    // ad game to user
    userService.addGame(id, game);
  }

    @GetMapping("users/{userId}/games/blackCards")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CardGetDTO getBlackCardFromUser(@RequestHeader(value = "authorization", required = false) String token,
                                      @PathVariable(value = "userId") Long id) {

        userService.checkSpecificAccess(token, id);
        BlackCard bcRandomUser = userService.getBlackCardFromRandomUser(id);

        return DTOMapper.INSTANCE.convertEntityToCardGetDTO(bcRandomUser);
    }

    /**
    * User gets his white Cards
     */
    @GetMapping("users/{userId}/games/cards")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<CardGetDTO> getWhiteCardsFromUser(@RequestHeader(value = "authorization", required = false) String token,
                                                @PathVariable(value = "userId") Long id) {
      // check token
      userService.checkSpecificAccess(token, id); // throws 401, 404
      List <WhiteCard> cards = userService.getWhiteCards(id);

      List<CardGetDTO> cardGetDTO= new ArrayList<>();
      for (WhiteCard card : cards){
          cardGetDTO.add(DTOMapper.INSTANCE.convertEntityToCardGetDTO(card));
      }

      return cardGetDTO;

    }

    /**
     * get a Play with a white card to vote on
     * and the userId from the user that played that white card
     */
    @GetMapping("/users/{userId}/games/{gameId}/vote")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayGetDTO getPlay(@RequestHeader(value = "authorization", required = false) String token,
                        @PathVariable(value = "gameId") Long gameId, @PathVariable(value = "userId") Long id) {

        userService.checkSpecificAccess(token, id); // throws 401, 404

        // search for random Play with this gameId
        Play play = gameService.getRandomPlay(gameId);
        return DTOMapper.INSTANCE.convertEntityToPlayGetDTO(play);
    }

    /**
     * set Like to WhiteCard
     */
    @PutMapping("/users/{userId}/games/{gameId}/vote")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void voteCard(@RequestHeader(value = "authorization", required = false) String token,
                        @PathVariable(value = "gameId") Long gameId, @PathVariable(value = "userId") Long id
                        ) {

        userService.checkSpecificAccess(token, id); // throws 401, 404
        //TODO
        // delete Play
        // make connection between user
        //

        //gameService.setPlayLike(gameId);


    }

    /**
     * create Play of a player
     */
    @PostMapping("/users/{userId}/games/{gameId}/cards/{cardId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void createPlay(@RequestHeader(value = "authorization", required = false) String token,
                         @PathVariable(value = "gameId") Long gameId,
                         @PathVariable(value = "userId") Long id, @PathVariable(value = "cardId") Long cardId){

        userService.checkSpecificAccess(token, id); // throws 401, 404

        gameService.createPlay(id, gameId, cardId);


    }
}
