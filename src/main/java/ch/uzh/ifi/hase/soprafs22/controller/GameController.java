package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.entity.WhiteCard;
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

  @GetMapping("/games/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<CardGetDTO> getBlackCard(@RequestHeader(value = "authorization", required = false) String token,
                                      @PathVariable(value = "id") Long id) {

    // check if source of query has access token
    userService.checkSpecificAccess(token, id);
    List<BlackCard> cards = gameService.getCards();
    List<CardGetDTO> blackCardGetDTOS= new ArrayList<>();
    for (BlackCard card : cards){
      blackCardGetDTOS.add(DTOMapper.INSTANCE.convertEntityToCardGetDTO(card));
    }

    return blackCardGetDTOS;
  }

  @PostMapping("/games/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public CardGetDTO createGame(@RequestHeader(value = "authorization", required = false) String token,
                                    @PathVariable(value = "id") Long id,
                                    @RequestBody CardPostDTO blackCardPostDTO) {
    System.out.println(blackCardPostDTO);
    userService.checkSpecificAccess(token, id);

    BlackCard userInputCard = DTOMapper.INSTANCE.convertGamePostDTOToEntity(blackCardPostDTO);

    return DTOMapper.INSTANCE.convertEntityToCardGetDTO(userInputCard);
  }

    @GetMapping("/games/{id}/blackCards")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public CardGetDTO getBlackCardFromUser(@RequestHeader(value = "authorization", required = false) String token,
                                      @PathVariable(value = "id") Long id) {

        userService.checkSpecificAccess(token, id);
        BlackCard bcRandomUser = userService.getBlackCardFromRandomUser(id);

        return DTOMapper.INSTANCE.convertEntityToCardGetDTO(bcRandomUser);
    }

    @GetMapping("/games/{id}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<CardGetDTO> getCards(@RequestHeader(value = "authorization", required = false) String token,
                                                @PathVariable(value = "id") Long id) {

        userService.checkSpecificAccess(token, id);
        List <WhiteCard> cards = userService.getWhiteCards(id);

        List<CardGetDTO> cardGetDTO= new ArrayList<>();
        for (WhiteCard card : cards){
            cardGetDTO.add(DTOMapper.INSTANCE.convertEntityToCardGetDTO(card));
        }

        return cardGetDTO;

    }

}
