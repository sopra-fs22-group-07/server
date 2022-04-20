package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.BlackCardRepository;
import ch.uzh.ifi.hase.soprafs22.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BlackCardRepository blackCardRepository;

    @InjectMocks
    private GameService gameService;


    private User testUser;

    private Game testGame;

    private BlackCard blackCard;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given active Game
        testGame = new Game();
        blackCard = new BlackCard();
        testGame.setId(1L);
        testGame.setUserId(1L);
        testGame.setBlackCard(blackCard);
        testGame.setCreationTime(new Date());
        testGame.setGameStatus(GameStatus.ACTIVE);

        // when -> any object is being save in the gameRepository -> return the dummy
        Mockito.when(gameRepository.saveAndFlush(Mockito.any())).thenReturn(testGame);
    }


    @Test
    void getNRandomBlackCards_success() {
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
        blackCard = new BlackCard();
        pastGame.setId(2L);
        pastGame.setUserId(2L);
        pastGame.setBlackCard(blackCard);
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
        // Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(testGame);

        // Game game = gameService.getGameById(1L);

    }

    @Test
    void createGame_success() {
        Game game = gameService.createGame(blackCard, 1L);

        // test if game is equal to testGame (expected, actual)
        assertEquals(testGame.getId(), game.getId());
        assertEquals(testGame.getCreationTime(), game.getCreationTime());
        assertEquals(testGame.getGameStatus(), game.getGameStatus());
        assertEquals(testGame.getBlackCard(), game.getBlackCard());
    }

    @Test
    void createPlay() {
    }

    @Test
    void putPlayInGame() {
    }

    @Test
    void getNRandomWhiteCards() {
    }

    @Test
    void deletePlay() {
    }

    @Test
    void getBlackCardById() {
    }

    @Test
    void getWhiteCardById() {
    }
}
