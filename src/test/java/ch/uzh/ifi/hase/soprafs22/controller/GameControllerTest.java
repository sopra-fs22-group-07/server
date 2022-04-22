package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.CardPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.GameVotePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.service.GameService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
class GameControllerTest {
    WhiteCard whiteCard1 = new WhiteCard();
    WhiteCard whiteCard2 = new WhiteCard();
    BlackCard blackCard = new BlackCard();
    List<BlackCard> bCards = new ArrayList<>();
    List<WhiteCard> wCards = new ArrayList<>();
    CardPostDTO blackCardPostDTO = new CardPostDTO();
    GameVotePutDTO gameVotePutDTO = new GameVotePutDTO();
    Game game = new Game();
    User user = new User();
    User otherUser = new User();
    String token = "token";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void before() {
        whiteCard1.setId(1L);
        whiteCard1.setText("card 1");
        whiteCard2.setId(2L);
        whiteCard2.setText("card 2");
        blackCard.setId(3L);
        //blackCard.setNrOfBlanks(1);
        blackCard.setText("black");
        user.setId(11L);
        user.setUsername("username");
        user.setName("name");
        user.setPassword("password");
        user.setToken(token);
        otherUser.setId(22L);
        otherUser.setUsername("otherUsername");
        otherUser.setName("otherName");
        otherUser.setPassword("otherPassword");
        game.setId(111L);
        game.setBlackCard(blackCard);
        game.setUserId(user.getId());
        bCards.add(blackCard);
        wCards.add(whiteCard1);
        wCards.add(whiteCard2);
        blackCardPostDTO.setId(blackCard.getId());
        gameVotePutDTO.setUserId(otherUser.getId());
    }

    @BeforeEach
    void setUpForAuthorization() {
        doNothing().when(userService).checkSpecificAccess(isA(String.class), isA(Long.class));
    }

    @Test
    void givenBlackCards_whenGetBlackCards_thenOldCard() throws Exception {
        given(userService.getCurrentBlackCards(isA(Long.class))).willReturn(bCards);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/games", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text",is(blackCard.getText())));

    }

    @Test
    void givenBlackCards_whenGetBlackCards_thenNewCard() throws Exception {
        given(userService.getCurrentBlackCards(isA(Long.class))).willReturn(null);
        given(gameService.getNRandomBlackCards(isA(Integer.class))).willReturn(bCards);
        doNothing().when(userService).assignBlackCardsToUser(isA(Long.class), eq(bCards));

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/games", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text",is(blackCard.getText())));
    }

    @Test
    void givenUser_createGame() throws Exception {
        doNothing().when(userService).updateActiveGameIfNecessary(isA(Long.class));
        doNothing().when(userService).checkBlackCard(isA(Long.class), isA(BlackCard.class));
        given(userService.userHasNoActiveGame(isA(Long.class))).willReturn(true);
        given(gameService.getBlackCardById(blackCard.getId())).willReturn(blackCard);

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/games", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(blackCardPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    void givenBlackCards_whenGetBlackCardFromRandomUser() throws Exception {
        given(gameService.getGameFromRandomUser(isA(Long.class))).willReturn(game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/1/games/blackCards", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId",is(111)))
                .andExpect(jsonPath("$.blackCard.id",is(3)))
                .andExpect(jsonPath("$.blackCard.text",is(blackCard.getText())));
    }

    @Test
    void givenWhiteCards_whenGetWhiteCardsFromUser() throws Exception {
        given(userService.getWhiteCards(isA(Long.class))).willReturn(wCards);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/games/whiteCards", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].text", is(whiteCard1.getText())))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].text", is(whiteCard2.getText())));
    }

    @Test
    void givenGame_whenGetGame() throws Exception {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(gameService.getGame(Mockito.any(),Mockito.anyList())).willReturn(game);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/games/vote", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken());

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId",is(111)))
                .andExpect(jsonPath("$.blackCard.id",is(3)))
                .andExpect(jsonPath("$.blackCard.text",is(blackCard.getText())))
                .andExpect(jsonPath("$.userId", is(11)));
    }

    @Test
    void givenSecondLike_whenVoteCard() throws Exception {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.getUserById(otherUser.getId())).willReturn(otherUser);
        given(gameService.getGameById(game.getId())).willReturn(game);
        given(userService.isGameBelongingToUser(game, user)).willReturn(true);
        given(userService.otherUserLikesUser(user, otherUser)).willReturn(true);

        gameVotePutDTO.setLike(true);
        // when
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/games/{gameId}/vote", user.getId(), game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(gameVotePutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(otherUser.getName())))
                .andExpect(jsonPath("$.username", is(otherUser.getUsername())));
        verify(userService).createMatch(user, otherUser);
    }

    @Test
    void givenFirstLike_whenVoteCard() throws Exception {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.getUserById(otherUser.getId())).willReturn(otherUser);
        given(gameService.getGameById(game.getId())).willReturn(game);
        given(userService.isGameBelongingToUser(game, user)).willReturn(true);
        given(userService.otherUserLikesUser(user, otherUser)).willReturn(false);

        gameVotePutDTO.setLike(true);
        // when
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/games/{gameId}/vote", user.getId(), game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(gameVotePutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(userService).doesMatchExist(user, otherUser);
        verify(userService).setUserLikesOtherUser(user, otherUser);
        verifyNoInteractions(mock(UserGetDTO.class));
    }

    @Test
    void givenNoLike_whenVoteCard() throws Exception {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.getUserById(otherUser.getId())).willReturn(otherUser);
        given(gameService.getGameById(game.getId())).willReturn(game);
        given(userService.isGameBelongingToUser(game, user)).willReturn(true);
        given(userService.otherUserLikesUser(user, otherUser)).willReturn(true);

        gameVotePutDTO.setLike(false);
        // when
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/games/{gameId}/vote", user.getId(), game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(gameVotePutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(userService).doesMatchExist(user, otherUser);
        verifyNoInteractions(mock(UserGetDTO.class));
    }

    @Test
    void wrongUse_whenVoteCard1() throws Exception {
        // will return the same user for user and otherUser
        given(userService.getUserById(isA(Long.class))).willReturn(user);

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/games/{gameId}/vote", user.getId(), game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(gameVotePutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }
    @Test
    void wrongUse_whenVoteCard2() throws Exception {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(userService.getUserById(otherUser.getId())).willReturn(otherUser);

        given(userService.isGameBelongingToUser(Mockito.any(),eq(user))).willReturn(false);

        // when
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/games/{gameId}/vote", user.getId(), game.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(gameVotePutDTO));

        // then
        mockMvc.perform(putRequest).andExpect(status().isBadRequest());
    }

    @Test
    void givenPlay_whenSubmitWhiteCard() throws Exception {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(gameService.getGameById(game.getId())).willReturn(game);
        given(gameService.getWhiteCardById(whiteCard1.getId())).willReturn(whiteCard1);
        given(userService.isGameBelongingToUser(game,user)).willReturn(false);
        given(userService.isWhiteCardBelongingToUser(whiteCard1, user.getId())).willReturn(true);
        given(userService.hasUserAlreadyPlayInGame(Mockito.any(),Mockito.any())).willReturn(false);

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/whiteCards/{cardId}", user.getId(), whiteCard1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(blackCardPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated());
        verify(gameService).putPlayInGame(Mockito.any(), Mockito.any());
        verify(userService).deleteWhiteCard(user.getId(), whiteCard1);
    }

    @Test
    void wrongUse_whenSubmitWhiteCard1() throws Exception {
        given(userService.isGameBelongingToUser(Mockito.any(),Mockito.any())).willReturn(true);

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/whiteCards/{cardId}", user.getId(), whiteCard1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(blackCardPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }

    @Test
    void wrongUse_whenSubmitWhiteCard2() throws Exception {
        given(userService.getUserById(user.getId())).willReturn(user);
        given(gameService.getGameById(game.getId())).willReturn(game);
        given(gameService.getWhiteCardById(whiteCard1.getId())).willReturn(whiteCard1);
        given(userService.isGameBelongingToUser(game,user)).willReturn(false);
        given(userService.isWhiteCardBelongingToUser(whiteCard1, user.getId())).willReturn(false);

        // when
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/whiteCards/{cardId}", user.getId(), whiteCard1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken())
                .content(asJsonString(blackCardPostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }

  /**
   * Helper Method to convert PostDTOs into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   *
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}