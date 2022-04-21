package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BlackCardRepository blackCardRepository;

    @Mock
    private WhiteCardRepository whiteCardRepository;

    @Mock
    private PlayRepository playRepository;

    @InjectMocks
    private GameService gameService;

    private User testUser;

    private Game testGame;

    private BlackCard testBlackCard;

    private WhiteCard testWhiteCard;

    private Play testPlay;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given active Game
        testGame = new Game();
        testBlackCard = new BlackCard();
        testBlackCard.setText("gap");
        testBlackCard.setId(1L);
        testGame.setId(1L);
        testGame.setUserId(1L);
        testGame.setBlackCard(testBlackCard);
        testGame.setCreationTime(new Date());
        testGame.setGameStatus(GameStatus.ACTIVE);

        // when -> any object is being save in the gameRepository -> return the dummy
        Mockito.when(gameRepository.saveAndFlush(Mockito.any())).thenReturn(testGame);

        // given testPlay, without gameId (gets tested by putPlayInGame_success)
        testPlay = new Play();
        testWhiteCard = new WhiteCard();
        testWhiteCard.setId(1L);
        testWhiteCard.setText("funny stuff");
        testPlay.setCard(testWhiteCard);
        testPlay.setUserId(1L);

        // when -> any object is being save in the gameRepository -> return the dummy
        Mockito.when(playRepository.saveAndFlush(Mockito.any())).thenReturn(testPlay);
    }


    @Test
    void getNRandomBlackCards_success() {
        //TODO
        final int totalCards = 50;
        final int requestedCards = 12;
        // create some cards

        List<BlackCard> cards = new ArrayList<>();
        for (long i = 0; i < totalCards; i++) {
            BlackCard blackCard = new BlackCard();
            blackCard.setText("Test text" + i);
            blackCard.setId(i);
            cards.add(blackCard);
        }
        Mockito.when(blackCardRepository.count()).thenReturn((long) totalCards);
        // don't take a random page, just the first one.
        PageRequest pr = PageRequest.of(1, requestedCards);
        List<BlackCard> expected = cards.subList(0, requestedCards);
        Page<BlackCard> page = new PageImpl<>(expected);
        System.out.println(page);
        Mockito.when(blackCardRepository.findAll(pr)).thenReturn(page);

        List<BlackCard> actual = gameService.getNRandomBlackCards(requestedCards);
        assertEquals(new HashSet<>(expected), new HashSet<>(actual));
    }

    @Test
    void getGame_returnActiveGame_success() {

        List<Game> pastGame = new ArrayList<>();

        Game  game = gameService.getGame(testGame, pastGame);
        assertEquals(testGame.getId(), game.getId());
        assertEquals(testGame.getCreationTime(), game.getCreationTime());
        assertEquals(testGame.getGameStatus(), game.getGameStatus());
        assertEquals(testGame.getBlackCard(), game.getBlackCard());
    }

    @Test
    void getGame_returnPastGame_success() {
        List<Game> pastGames = new ArrayList<>();

        Game pastGame = new Game();
        BlackCard bc = new BlackCard();
        pastGame.setId(2L);
        pastGame.setUserId(2L);
        pastGame.setBlackCard(bc);
        pastGame.setCreationTime(new Date());
        pastGame.setGameStatus(GameStatus.INACTIVE);

        pastGames.add(pastGame);

        Game game = gameService.getGame(testGame, pastGames);
        assertEquals(pastGame.getId(), game.getId());
        assertEquals(pastGame.getCreationTime(), game.getCreationTime());
        assertEquals(pastGame.getGameStatus(), game.getGameStatus());
        assertEquals(pastGame.getBlackCard(), game.getBlackCard());
    }


    @Test
    void getGameById_success() {
        // then
        Mockito.when(gameRepository.findByGameId(1L)).thenReturn(testGame);

        Game game = gameService.getGameById(1L);
        // test if game is equal to testGame (expected, actual)
        assertEquals(testGame.getId(), game.getId());
        assertEquals(testGame.getCreationTime(), game.getCreationTime());
        assertEquals(testGame.getGameStatus(), game.getGameStatus());
        assertEquals(testGame.getBlackCard(), game.getBlackCard());
    }

    @Test
    void getGameById_fail() {
        // then
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(null);
        // expect exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.getGameById(2L);
        });
        assertEquals("404 NOT_FOUND \"game does not exist\"", exception.getMessage());
    }

    @Test
    void createGame_success() {
        Game game = gameService.createGame(testBlackCard, 1L);

        // test if game is equal to testGame (expected, actual)
        assertEquals(testGame.getId(), game.getId());
        assertEquals(testGame.getCreationTime(), game.getCreationTime());
        assertEquals(testGame.getGameStatus(), game.getGameStatus());
        assertEquals(testGame.getBlackCard(), game.getBlackCard());
    }

    @Test
    void createPlay_success() {
        // given
        WhiteCard testWhiteCard = new WhiteCard();
        testWhiteCard.setId(1L);
        testWhiteCard.setText("funny stuff");

        Long userId = 2L;

        // then
        Mockito.when(whiteCardRepository.findById(1L)).thenReturn(testWhiteCard);

        // test
        Play play = gameService.createPlay(userId, 1L);

        // test if game is equal to testGame (expected, actual)
        assertEquals(play.getCard(), testWhiteCard);
        assertEquals(play.getUserId(), userId);
    }

    @Test
    void putPlayInGame_success() {
        gameService.putPlayInGame(testGame, testPlay);
        // test if gameId is correctly set
        assertEquals(testPlay.getGameId(), testGame.getId());
        // test if play is in List of plays in game
        assertEquals(true, testGame.getPlays().contains(testPlay));
    }

    @Test
    void getNRandomWhiteCards() {
    }

    @Test
    void deletePlay_success() {
        Play deletePlay = new Play();
        WhiteCard deleteWhiteCard = new WhiteCard();
        deleteWhiteCard.setId(3L);
        deleteWhiteCard.setText("unfunny stuff");
        deletePlay.setCard(deleteWhiteCard);
        deletePlay.setUserId(3L);

        testGame.enqueuePlay(deletePlay);

        // then
        gameService.deletePlay(testGame,3L);

        // test if play is in List of plays in game
        assertEquals(false, testGame.getPlays().contains(deletePlay));
    }

    @Test
    void getBlackCardById_success() {
        // then
        Mockito.when(blackCardRepository.findById(1L)).thenReturn(testBlackCard);

        BlackCard bc = gameService.getBlackCardById(1L);
        // test if game is equal to testGame (expected, actual)
        assertEquals(testBlackCard.getId(), bc.getId());
        assertEquals(testBlackCard.getText(), bc.getText());
    }

    @Test
    void getBlackCardById_fail() {
        // then
        Mockito.when(blackCardRepository.findById(2L)).thenReturn(null);
        // expect exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.getBlackCardById(2L);
        });
        assertEquals("404 NOT_FOUND \"black card with cardId 2 does not exit\"", exception.getMessage());
    }

    @Test
    void getWhiteCardById_success() {
        // then
        Mockito.when(whiteCardRepository.findById(1L)).thenReturn(testWhiteCard);

        WhiteCard wc = gameService.getWhiteCardById(1L);
        // test if game is equal to testGame (expected, actual)
        assertEquals(testWhiteCard.getId(), wc.getId());
        assertEquals(testWhiteCard.getText(), wc.getText());
    }

    @Test
    void getWhiteCardById_fail() {
        // then
        Mockito.when(whiteCardRepository.findById(2L)).thenReturn(null);
        // expect exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            gameService.getWhiteCardById(2L);
        });
        assertEquals("404 NOT_FOUND \"white card with cardId 2 does not exit\"", exception.getMessage());
    }

    @Test
    void getGameFromRandomUser_success() {
        List<Game> games = new ArrayList<>();
        games.add(testGame);
        Page<Game> somePage = new PageImpl<>(games);

        // then
        Mockito.when(gameRepository.countOtherUserWithActiveGameThatWasNotPlayedOn(1L)).thenReturn(101L);
        Mockito.when(gameRepository.getOtherUserWithActiveGameThatWasNotPlayedOn(Mockito.any(PageRequest.class),
                eq(1L))).thenReturn(somePage);

        // test
        Game game = gameService.getGameFromRandomUser(1L);
        // test if game is equal to testGame
        assertEquals(testGame.getId(), game.getId());
        assertEquals(testGame.getCreationTime(), game.getCreationTime());
        assertEquals(testGame.getGameStatus(), game.getGameStatus());
        assertEquals(testGame.getBlackCard(), game.getBlackCard());
    }
}
