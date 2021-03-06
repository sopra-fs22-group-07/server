package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@TestPropertySource(
        locations = "application-integrationtest.properties")
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private BlackCardRepository blackCardRepository;

    @Mock
    private WhiteCardRepository whiteCardRepository;

    @Mock
    private PlayRepository playRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;
    private User testUser;

    private BlackCard testBlackCard;

    private WhiteCard testWhiteCard;

    private Play testPlay;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("1234");
        testUser.setGender(Gender.OTHER);
        testUser.setBirthday(new Date());

        // given active Game
        testGame = new Game();
        testBlackCard = new BlackCard();
        testBlackCard.setText("gap");
        testBlackCard.setId(1L);
        testGame.setId(1L);
        testGame.setUser(testUser);
        testGame.setBlackCard(testBlackCard);
        testGame.setCreationTime(new Date());
        testGame.setGameStatus(GameStatus.ACTIVE);

        // when -> any object is being safe in the gameRepository -> return the dummy
        Mockito.when(gameRepository.saveAndFlush(Mockito.any())).thenReturn(testGame);

        // given testPlay, without gameId (gets tested by putPlayInGame_success)
        testPlay = new Play();
        testWhiteCard = new WhiteCard();
        testWhiteCard.setId(1L);
        testWhiteCard.setText("funny stuff");
        testPlay.setCard(testWhiteCard);
        testPlay.setUserId(1L);

        // when -> any object is being safe in the gameRepository -> return the dummy
        Mockito.when(playRepository.saveAndFlush(Mockito.any())).thenReturn(testPlay);
    }

    @Test
    void getNRandomBlackCards_success_single() {
        // then
        Mockito.when(blackCardRepository.getAllIds()).thenReturn(List.of(testBlackCard.getId()));
        Mockito.when(blackCardRepository.findById(1L)).thenReturn(testBlackCard);

        List<BlackCard> randomCards = gameService.getNRandomBlackCards(1);
        assertTrue(randomCards.contains(testBlackCard));
    }

    @Test
    void getNRandomBlackCards_success_multiple() {
        BlackCard secondBlackCard = new BlackCard();
        secondBlackCard.setId(2L);

        // then
        Mockito.when(blackCardRepository.getAllIds()).thenReturn(List.of(testBlackCard.getId(), secondBlackCard.getId()));
        Mockito.when(blackCardRepository.findById(1L)).thenReturn(testBlackCard);
        Mockito.when(blackCardRepository.findById(2L)).thenReturn(secondBlackCard);

        List<BlackCard> randomCards = gameService.getNRandomBlackCards(2);
        assertTrue(randomCards.contains(testBlackCard) || randomCards.contains(secondBlackCard));
        assertEquals(2, randomCards.size());
    }

    @Test
    void getNRandomBlackCards_noCards() {
        Mockito.when(blackCardRepository.getAllIds()).thenReturn(List.of(testBlackCard.getId()));

        List<BlackCard> randomCards = gameService.getNRandomBlackCards(0);
        assertTrue(randomCards.isEmpty());

    }

    @Test
    void getNRandomWhiteCards_success() {
        // then
        Mockito.when(whiteCardRepository.getAllIds()).thenReturn(List.of(testWhiteCard.getId()));
        Mockito.when(whiteCardRepository.findById(1L)).thenReturn(testWhiteCard);

        List<WhiteCard> randomCards = gameService.getNRandomWhiteCards(1);

        assertEquals(1, randomCards.size());
        assertTrue(randomCards.contains(testWhiteCard));
    }

    @Test
    void getNRandomWhiteCards_success_multiple() {
        WhiteCard secondWhiteCard = new WhiteCard();
        secondWhiteCard.setId(2L);
        // then
        Mockito.when(whiteCardRepository.getAllIds()).thenReturn(List.of(testWhiteCard.getId(), secondWhiteCard.getId()));
        Mockito.when(whiteCardRepository.findById(1L)).thenReturn(testWhiteCard);
        Mockito.when(whiteCardRepository.findById(2L)).thenReturn(secondWhiteCard);

        List<WhiteCard> randomCards = gameService.getNRandomWhiteCards(2);

        assertEquals(2, randomCards.size());
        assertTrue(randomCards.contains(testWhiteCard) || randomCards.contains(secondWhiteCard));
    }

    @Test
    void getNRandomWhiteCards_noCards() {
        Mockito.when(whiteCardRepository.getAllIds()).thenReturn(List.of(testWhiteCard.getId()));

        List<WhiteCard> randomCards = gameService.getNRandomWhiteCards(0);
        assertTrue(randomCards.isEmpty());

    }

    @Test
    void getGame_fail() {
        List<Game> emptyList = new ArrayList<>();
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> gameService.getGame(emptyList));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    @Test
    void getGame_returnActiveGame_success() {
        List<Game> games = new ArrayList<>();
        games.add(testGame);

        Game  game = gameService.getGame(games);
        assertEquals(testGame.getId(), game.getId());
        assertEquals(testGame.getCreationTime(), game.getCreationTime());
        assertEquals(testGame.getGameStatus(), game.getGameStatus());
        assertEquals(testGame.getBlackCard(), game.getBlackCard());
    }

    @Test
    void getGame_returnPastGame_success() {
        List<Game> games = new ArrayList<>();

        Game pastGame = new Game();
        BlackCard bc = new BlackCard();
        pastGame.setId(2L);
        pastGame.setUser(testUser);
        pastGame.setBlackCard(bc);
        pastGame.setCreationTime(new Date());
        pastGame.setGameStatus(GameStatus.INACTIVE);

        games.add(pastGame);
        games.add(testGame);

        Game game = gameService.getGame(games);
        assertEquals(pastGame.getId(), game.getId());
        assertEquals(pastGame.getCreationTime(), game.getCreationTime());
        assertEquals(pastGame.getGameStatus(), game.getGameStatus());
        assertEquals(pastGame.getBlackCard(), game.getBlackCard());
    }


    @Test
    void getGameById_success() {
        // then
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

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
        Mockito.when(gameRepository.findById(2L)).thenReturn(Optional.empty());
        // expect exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                gameService.getGameById(2L));
        assertEquals("404 NOT_FOUND \"game does not exist\"", exception.getMessage());
    }

    @Test
    void createGame_success() {
        Game game = gameService.createGame(testBlackCard, testUser);

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
        assertTrue(testGame.getPlays().contains(testPlay));
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
        assertFalse(testGame.getPlays().contains(deletePlay));
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
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.getBlackCardById(2L));
        assertEquals("404 NOT_FOUND \"black card with cardId 2 does not exist\"", exception.getMessage());
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
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.getWhiteCardById(2L));
        assertEquals("404 NOT_FOUND \"white card with cardId 2 does not exist\"", exception.getMessage());
    }


    @Test
    void getGameFromRandomUser_success() {
        List<Game> games = new ArrayList<>();
        games.add(testGame);
        Page<Game> somePage = new PageImpl<>(games);

        // then
        Mockito.when(gameRepository.countOtherUserWithActiveGameThatWasNotPlayedOn(
                eq(1L),
                        //eq(testUser),
                        eq(testUser.getGender().name()),
                        Mockito.any(),
                        Mockito.any()))
                        //eq(testUser.getBlockedUsers()),
                        //eq(testUser.getMatchedUsers())))
                .thenReturn(101L);
        Mockito.when(gameRepository.getOtherUserWithActiveGameThatWasNotPlayedOn(
                Mockito.any(PageRequest.class),
                        eq(1L),
                        // eq(testUser),
                        eq(testUser.getGender().name()),
                        Mockito.any(),
                        Mockito.any()))
                        // eq(testUser.getBlockedUsers()),
                        // eq(testUser.getMatchedUsers())))
                .thenReturn(somePage);

        // test
        Game game = gameService.getGameFromRandomUser(testUser);
        // test if game is equal to testGame
        assertEquals(testGame.getId(), game.getId());
        assertEquals(testGame.getCreationTime(), game.getCreationTime());
        assertEquals(testGame.getGameStatus(), game.getGameStatus());
        assertEquals(testGame.getBlackCard(), game.getBlackCard());
    }


    @Test
    void getGameFromRandomUser_throwNotFound() {

        // then
        Mockito.when(gameRepository.countOtherUserWithActiveGameThatWasNotPlayedOn(eq(1L),
                        //eq(testUser),
                        eq(testUser.getGender().name()),
                        Mockito.any(),
                        Mockito.any()))
                        //eq(testUser.getBlockedUsers()),
                        // eq(testUser.getMatchedUsers())))
                .thenReturn(0L);

        // test
        // expect exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> gameService.getGameFromRandomUser(testUser));
        assertEquals("404 NOT_FOUND \"There is no black card of another user left\"", exception.getMessage());
    }

    @Test
    void haversineDistance_correctOutput() {

        double lat1 = 0;
        double lon1 = 0;
        double lat2 = 10;
        double lon2 = 10;
        
        double distance = GameService.haversineDistance(lat1, lon1, lat2, lon2);

        // test if result is the same when rounded to 2 decimal places.
        // actual result computed using: https://www.vcalc.com/wiki/vCalc/Haversine+-+Distance (not the same source as for the code)
        assertEquals(1568.52, distance, 0.01);
    }

    @Test
    void haversineDistance_largeDistance_correctOutput() {
            
            double lat1 = -100;
            double lon1 = -100;
            double lat2 = 100;
            double lon2 = 100;
    
            double distance = GameService.haversineDistance(lat1, lon1, lat2, lon2);
    
            // test if result is the same when rounded to nearest integer.
            // actual result computed using: https://www.vcalc.com/wiki/vCalc/Haversine+-+Distance (not the same source as for the code)
            assertEquals(19630, distance, 1);
    }

}
