package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Play;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Test class for the GameResource REST resource.
 *
 * @see GameService
 */
@WebAppConfiguration
@SpringBootTest
@TestPropertySource(
        locations = "application-integrationtest.properties")
class GameServiceIntegrationTest {

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Qualifier("playRepository")
    @Autowired
    private PlayRepository playRepository;

    @Mock
    private BlackCardRepository cardRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    private BlackCard testBlackCard;
    private User testUser;


    @BeforeEach
    public void setup() {
        testBlackCard = new BlackCard();
        testBlackCard.setText("gap");
        testBlackCard.setId(1L);

        testUser = new User();

        testUser.setUsername("username");
        testUser.setName("name");
        testUser.setPassword("password");
        testUser.setToken("1234");
        testUser.setGender(Gender.FEMALE);
        testUser.setBirthday(new Date());

        User createdUser = userService.createUser(testUser);

        Mockito.when(cardRepository.saveAndFlush(Mockito.any())).thenReturn(testBlackCard);
    }

    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
        gameRepository.deleteAll();
        playRepository.deleteAll();
    }

    @Test
    void createGame_validInputs_success() {
        // given
        assertNull(gameRepository.findById(1L));

        Game testGame = new Game();
        Long testUserId = 2L;

        testGame.setBlackCard(testBlackCard);
        testGame.setUser(testUser);
        testGame.setGameStatus(GameStatus.ACTIVE);

        // when
        Game createdGame = gameService.createGame(testBlackCard, testUser);

        // then
        assertEquals(testGame.getBlackCard(), createdGame.getBlackCard());
        assertEquals(testGame.getUser(), createdGame.getUser());
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
        testGame.setUser(testUser);
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
