package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Play;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for the GameResource REST resource.
 *
 * @see GameService
 */
@WebAppConfiguration
@SpringBootTest
class GameServiceIntegrationTest {

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("playRepository")
    @Autowired
    private PlayRepository playRepository;

    @Autowired
    private GameService gameService;

    private BlackCard testBlackCard;

    @BeforeEach
    public void setup() {
        gameRepository.deleteAll();
        playRepository.deleteAll();
        testBlackCard = new BlackCard();
        testBlackCard.setText("gap");
        testBlackCard.setId(1L);
    }

    @Test
    void createGame_validInputs_success() {
        // given
        assertNull(gameRepository.findById(1L));

        Game testGame = new Game();
        Long testUserId = 2L;

        testGame.setBlackCard(testBlackCard);
        testGame.setUserId(testUserId);
        testGame.setGameStatus(GameStatus.ACTIVE);

        // when
        Game createdGame = gameService.createGame(testBlackCard, testUserId);

        // then
        assertEquals(testGame.getBlackCard(), createdGame.getBlackCard());
        assertEquals(testGame.getUserId(), createdGame.getUserId());
        assertEquals(GameStatus.ACTIVE, createdGame.getGameStatus());
    }

    @Test
    void putPlayInGame_validInput_success() {
        // given
        assertNull(playRepository.findById(1L));

        // Test Game
        Game testGame = new Game();
        Long testUserId = 2L;
        testGame.setBlackCard(testBlackCard);
        testGame.setUserId(testUserId);
        testGame.setGameStatus(GameStatus.ACTIVE);
        testGame.setId(1L);

        // Test Play
        Play testPlay = new Play();
        testPlay.setUserId(testUserId + 1);

        // when
        gameService.putPlayInGame(testGame, testPlay);

        // then
        assertTrue(testGame.getPlays().contains(testPlay));
        assertEquals(testPlay.getGameId(), testGame.getId());
    }


}
