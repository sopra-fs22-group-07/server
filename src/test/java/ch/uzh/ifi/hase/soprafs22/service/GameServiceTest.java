package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("1234");
        testUser.setGender(Gender.OTHER);

        // when -> any object is being save in the userRepository -> return the dummy
        // testUser
        // Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }


    @Test
    void getNRandomBlackCards() {
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
    void getGame() {
    }

    @Test
    void getGameById() {
    }

    @Test
    void createGame() {

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
