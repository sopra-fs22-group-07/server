package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
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
  public List<BlackCardGetDTO> getBlackCard(@RequestHeader(value = "authorization", required = false) String token,
                                      @PathVariable(value = "id") Long id) {

    // check if source of query has access token
    userService.checkSpecificAccess(token, id);
    List<BlackCard> cards = gameService.getCards();
    List<BlackCardGetDTO> blackCardGetDTOS= new ArrayList<>();
    for (BlackCard card : cards){
      blackCardGetDTOS.add(DTOMapper.INSTANCE.convertEntityToBlackCardGetDTO((BlackCard) card));
    }

    return blackCardGetDTOS;
  }

  @PostMapping("/games/{id}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public BlackCardGetDTO createGame(@RequestHeader(value = "authorization", required = false) String token,
                                    @PathVariable(value = "id") Long id,
                                    @RequestBody BlackCardPostDTO blackCardPostDTO) {
    System.out.println(blackCardPostDTO);
    userService.checkSpecificAccess(token, id);
    // TODO: 08.04.2022 Properly safe Card
    BlackCard userInputCard = DTOMapper.INSTANCE.convertGamePostDTOToEntity(blackCardPostDTO);

    return DTOMapper.INSTANCE.convertEntityToBlackCardGetDTO(userInputCard);
  }

}
