package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.Time;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserBlackCardsRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.testHelpers.UserFiller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(
        locations = "application-integrationtest.properties")
class UserServiceTest extends UserFiller {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserBlackCardsRepository userBlackCardsRepository;

  @Mock
  private MatchRepository matchRepository;

  @Mock
  private ChatRepository chatRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private User otherUser;
  private WhiteCard testWhiteCard;
  private List<WhiteCard> testWhiteCards;
  private BlackCard testBlackCard;
  private BlackCard otherBlackCard;
  private UserBlackCards userBlackCards;
  private Game testGame;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    // given
    testUser = fillUser(1L, "testName", "testUsername", "1234");
    testUser.setGender(Gender.OTHER);
    testUser.setBirthday(new Date());

    //other user for matching etc
    otherUser = fillUser(2L, "otherUser", "testOtherUsername", "1234");
    otherUser.setGender(Gender.OTHER);

    //give; white card for testing
    testWhiteCard = new WhiteCard();
    testWhiteCard.setId(11L);
    testWhiteCard.setText("CardText");
    testWhiteCards = new ArrayList<>();
    testWhiteCards.add(testWhiteCard);

    testBlackCard = new BlackCard();
    testBlackCard.setText("GapText");
    testBlackCard.setId(22L);
    otherBlackCard = new BlackCard();
    otherBlackCard.setId(33L);
    otherBlackCard.setText("some Text");

    testGame = new Game();
    testGame.setId(111L);
    testGame.setUser(testUser);
    testGame.setBlackCard(testBlackCard);
    testGame.setGameStatus(GameStatus.ACTIVE);
    userBlackCards = new UserBlackCards();
    userBlackCards.setBlackCards(new ArrayList<>(Collections.singleton(testBlackCard)));
    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);

    // given Chat
    Chat chat = new Chat();
    Mockito.when(chatRepository.save(Mockito.any())).thenReturn(chat);
  }

  @Test
  void getUsers_empty(){
      List<User> emptyUserList = new ArrayList<>();
      assertEquals(emptyUserList, userService.getUsers());
  }

    @Test
    void getUsers_not_empty(){
        User user = userService.createUser(testUser);
        List<User> checkList = Arrays.asList(user);
        List<User> userList = new ArrayList<>();
        userList.add(user);

        Mockito.when(userRepository.findAll()).thenReturn(checkList);

        assertEquals(userList, userService.getUsers());
    }

  @Test
  void createUser_validInputs_success() {
    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }


  @Test
  void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    assertEquals(HttpStatus.CONFLICT, e.getStatus());
  }

  @Test
  void logOutUser_success(){
      long id = testUser.getId();
        //when -> looking for testuser by id then return testUser
      Mockito.when(userRepository.findById(id)).thenReturn(testUser);

      //check if testUser.Status is Correct
      //Check if correct User get returned
      assertEquals(testUser, userService.logoutUser(testUser));
      assertEquals(UserStatus.OFFLINE, testUser.getStatus());
  }

  @Test
  void loginUser_success() {
      // given
      User inputUser = fillUser(testUser.getId(), "testName", "testUsername", "1234");

      Mockito.when(userRepository.findByUsername(inputUser.getUsername())).thenReturn(testUser);

      // when -> setup additional mocks for UserRepository
      User returnUser = userService.doLogin(inputUser);

      // then
      assertEquals(testUser.getId(), returnUser.getId());
      assertEquals(testUser.getName(), returnUser.getName());
      assertEquals(testUser.getUsername(), returnUser.getUsername());
      assertEquals(UserStatus.ONLINE, returnUser.getStatus());
  }

  @Test
  void loginUser_error() {
      // given -> a first user has already been created
      userService.createUser(testUser);
      User inputUser = fillUser(testUser.getId(), "testName", "testUsername", "abcd");

      Mockito.when(userRepository.findByUsername(inputUser.getUsername())).thenReturn(testUser);

      // then error, because different password
      ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.doLogin(inputUser));
      assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
  }

  @Test
  void username_available_success() {
    String username = "Available Username";

    userService.createUser(testUser);
    User inputUser = new User();
    inputUser.setUsername("Available Username");

    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    assertTrue(userService.isUsernameAvailable(username));
  }

  @Test
  void username_available_failure() {
    String username = testUser.getUsername();

    Mockito.when(userRepository.findByUsername(username)).thenReturn(testUser);

    assertFalse(userService.isUsernameAvailable(username));
  }
  
    @Test
    void updateUser_success_sameUser(){
      userService.createUser(testUser);
      User putUser = new User();
      putUser.setId(testUser.getId());
      putUser.setUsername("newUsername");
      putUser.setGender(Gender.MALE);

      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
      Mockito.when(userRepository.findByUsername(putUser.getUsername())).thenReturn(testUser);

      User updatedUser = userService.updateUser(putUser);

      assertEquals("newUsername", updatedUser.getUsername());
      assertEquals(Gender.MALE, updatedUser.getGender());
  }

    @Test
    void updateUser_success_UsernameIsFree(){
        userService.createUser(testUser);
        User putUser = new User();
        putUser.setId(testUser.getId());
        putUser.setUsername("newUsername");
        putUser.setGender(Gender.MALE);

        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        Mockito.when(userRepository.findByUsername(putUser.getUsername())).thenReturn(null);

        User updatedUser = userService.updateUser(putUser);

        assertEquals("newUsername", updatedUser.getUsername());
        assertEquals(Gender.MALE, updatedUser.getGender());
    }

    @Test
    void updateUser_Conflict(){ //Test what happens when user tries to change username to a name that already is taken
        //Second user, that has the newUsername, that testUser wants
        User conflictUser = fillUser(2L, "conflictName", "newUsername", "1234");
        conflictUser.setGender(Gender.OTHER);

        userService.createUser(testUser);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(conflictUser);

        User putUser = new User();
        putUser.setId(testUser.getId());
        putUser.setUsername("newUsername");
        putUser.setGender(Gender.MALE);

        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        Mockito.when(userRepository.findByUsername(putUser.getUsername())).thenReturn(conflictUser);

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.updateUser(putUser));
        assertEquals(HttpStatus.CONFLICT, e.getStatus());
    }

    @Test
    void getUserById_returns_user() {
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        assertEquals(testUser, userService.getUserById(testUser.getId()));
    }

    @Test
    void getUserById_returns_exception(){
        Mockito.when(userRepository.findById(1L)).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(1L));
    }

    @Test
    void checkSpecificAccess_true(){
        testUser.setToken("token");
        Mockito.when(userRepository.findByToken("token")).thenReturn(testUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        assertDoesNotThrow(() -> userService.checkSpecificAccess(testUser.getToken(), testUser.getId()));
    }

    @Test
    void checkSpecificAccess_false(){
        testUser.setToken("token");
        User otherUser = new User();
        otherUser.setUsername("other");
        otherUser.setPassword("other password");
        Mockito.when(userRepository.findByToken("token")).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> userService.checkSpecificAccess("token", 1L));

        Mockito.when(userRepository.findByToken("token")).thenReturn(testUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(otherUser);
        assertThrows(ResponseStatusException.class, () -> userService.checkSpecificAccess("token", 1L));
    }

    @Test
    void getWhiteCards_success_nonEmpty(){
      //User has a White Card
      testUser.setUserWhiteCards(testWhiteCards);
      long id = testUser.getId();

      Mockito.when(userRepository.findById(id)).thenReturn(testUser);

      assertEquals(testWhiteCards, userService.getWhiteCards(id));
    }

    @Test
    void getWhiteCards_success_Empty(){
        //User does not have a White Card
        long id = testUser.getId();

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);

        assertEquals(new ArrayList<>(), userService.getWhiteCards(id));
    }

    @Test
    void addGame_success(){
      long id = testUser.getId();

      Mockito.when(userRepository.findById(id)).thenReturn(testUser);

      //should add the testgame to active games of testUser
      userService.addGame(id, testGame);

      assertEquals(testGame, testUser.getActiveGame());
    }

    @Test
    void userHasNoActiveGame_true(){
        long id = testUser.getId();

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);

        //By default user shouldn't have an active game so should be true as no game added here
        assertTrue(userService.userHasNoActiveGame(id));
    }

    @Test
    void userHasNoActiveGame_false(){
        long id = testUser.getId();

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);
        //Game added to user
        userService.addGame(id, testGame);
        assertFalse(userService.userHasNoActiveGame(id));
    }

    @Test
    void assignWhiteCards_emptyList(){
        long id = testUser.getId();
        List<WhiteCard> emptyWhiteCardList = new ArrayList<>();

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);

        //OverWrite testUsers WhiteCards with an EmptyList of White Cards
        userService.assignWhiteCards(testUser.getId(), emptyWhiteCardList);

        assertEquals(emptyWhiteCardList, testUser.getUserWhiteCards());
    }

    @Test
    void assignWhiteCards_nonEmptyList(){
        long id = testUser.getId();
        //Creating white Card list with 2 whiteCards
        WhiteCard testWhiteCard1 = new WhiteCard();
        testWhiteCard1.setId(31L);
        testWhiteCard1.setText("CardText2");
        WhiteCard testWhiteCard2 = new WhiteCard();
        testWhiteCard1.setId(32L);
        testWhiteCard1.setText("CardText3");

        List<WhiteCard> WhiteCardList = new ArrayList<>();
        WhiteCardList.add(testWhiteCard1);
        WhiteCardList.add(testWhiteCard2);

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);

        //OverWrite testUsers WhiteCards with an EmptyList of White Cards
        userService.assignWhiteCards(testUser.getId(), WhiteCardList);

        assertEquals(WhiteCardList, testUser.getUserWhiteCards());
    }


    @Test
    void createMatch_success(){
        Match isMatch = userService.createMatch(testUser, otherUser);

        //Comparing the First and second object of the pair of the matches. IF they are the same
        //Then the match contains the same pairs
        //Couldn't find a way to compare matches otherwise as the match is initialized in the method
        assertEquals(testUser , isMatch.getUserPair().getObj1());
        assertEquals(otherUser, isMatch.getUserPair().getObj2());
    }

    @Test
    void deleteGameIfEmpty_emptyGame(){
      assertEquals(Collections.emptyList(), testUser.getPastGames());
      Game emptyGame = new Game();
      testUser.setActiveGame(emptyGame);
      assertEquals(Collections.emptyList(), testUser.getPastGames());
      //This moves the emptyGame to past Games
      testUser.flushGameToPastGames();
      userService.deleteGameIfEmpty(testUser, emptyGame);
      //getPastGames should now be empty as the emptygame got deleted
      assertEquals(Collections.emptyList(), testUser.getPastGames());
    }

    @Test
    void deleteGameIfEmpty_nonEmptyGame(){
        Play play = new Play();
        Game nonEmptyGame = new Game();
        nonEmptyGame.enqueuePlay(play);
        testUser.setActiveGame(nonEmptyGame);
        assertEquals(Collections.emptyList(), testUser.getPastGames());
        //This moves the nonEmptyGame to past Games
        testUser.flushGameToPastGames();
        userService.deleteGameIfEmpty(testUser, nonEmptyGame);
        //Should now not return empty list for past games
        assertNotEquals(Collections.emptyList(), testUser.getPastGames());
    }

    @Test
    void updateActiveGameIfNecessary_oldGame() {
      testGame.setCreationTime(new Date(0));
      testUser.setActiveGame(testGame);
      long id = testUser.getId();

      Mockito.when(userRepository.findById(id)).thenReturn(testUser);

      userService.updateActiveGameIfNecessary(testUser.getId());
        assertNull(testUser.getActiveGame());
    }

    @Test
    void updateActiveGameIfNecessary_newGame() {
        testUser.setActiveGame(testGame);

        long id = testUser.getId();

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);

        userService.updateActiveGameIfNecessary(testUser.getId());
        assertEquals(testGame, testUser.getActiveGame());
    }


    @Test
    void setMatch(){
      Match testMatch =  userService.createMatch(testUser, otherUser);
      testMatch.setMatchId(2L);

      //both users should now have the testmatch
      userService.setMatch(testMatch);

      //checking if both users have the match with the given id
      assertTrue(testUser.getMatches().contains(testMatch.getMatchId()));
        assertTrue(otherUser.getMatches().contains(testMatch.getMatchId()));
    }

    @Test
    void otherUserLikesUser_true(){
      //set the otheruser to like testUser
      userService.setUserLikesOtherUser(otherUser, testUser);
      assertTrue(userService.otherUserLikesUser(testUser, otherUser));
    }

    @Test
    void otherUserLikesUser_false(){
        //testUser by default isn't liked by other user
        assertFalse(userService.otherUserLikesUser(testUser, otherUser));
    }

    @Test
    void isGameBelongingToUser_true(){
      //by default we set testgame to belong to user
        testGame.setUser(testUser);
      assertTrue(userService.isGameBelongingToUser(testGame, testUser));
    }

    @Test
    void isGameBelongingToUser_false(){
        //we set the testgame to belong to other user by changing the userId of the testgame
        testGame.setUser(testUser);
        assertFalse(userService.isGameBelongingToUser(testGame, otherUser));
    }

    @Test
    void isWhiteCardBelongingToUser_true(){
      //assign the white cards to the user
      testUser.setUserWhiteCards(testWhiteCards);
      long id = testUser.getId();

      Mockito.when(userRepository.findById(id)).thenReturn(testUser);
      assertTrue(userService.isWhiteCardBelongingToUser(testWhiteCard, testUser.getId()));
    }

    @Test
    void isWhiteCardBelongingToUser_false(){
        //assign the white cards to the otherUser
        otherUser.setUserWhiteCards(testWhiteCards);
        long id = testUser.getId();

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);
        assertFalse(userService.isWhiteCardBelongingToUser(testWhiteCard, testUser.getId()));
    }

    @Test
    void deleteWhiteCard(){
      //Create a second white card to delete
        WhiteCard deleteCard = new WhiteCard();
        deleteCard.setId(44L);
        deleteCard.setText("CardText");
        testWhiteCards.add(deleteCard); //Add the card to delete to the whitecards for the user
        testUser.setUserWhiteCards(testWhiteCards);

        long id = testUser.getId();
        Mockito.when(userRepository.findById(id)).thenReturn(testUser);

        assertTrue(userService.isWhiteCardBelongingToUser(deleteCard, testUser.getId()));
        userService.deleteWhiteCard(testUser.getId(), deleteCard);

        assertTrue(userService.isWhiteCardBelongingToUser(testWhiteCard, testUser.getId()));//should still be there
        assertFalse(userService.isWhiteCardBelongingToUser(deleteCard, testUser.getId())); //should be deleted now
    }


    @Test
    void doesMatchExist_true(){
        Match testMatch =  userService.createMatch(testUser, otherUser);
        testMatch.setMatchId(222L);

        Mockito.when(matchRepository.countMatchByUserPair(testUser, otherUser)).thenReturn(1);
      Mockito.when(matchRepository.countMatchByUserPair(otherUser, testUser)).thenReturn(1);

      userService.setMatch(testMatch);
        //both users should now have the testMatch, so it should exist
        assertTrue(userService.doesMatchExist(testUser, otherUser), "expected a match between testUser and otherUser");
        assertTrue(userService.doesMatchExist(otherUser, testUser), "expected a match between otherUser and testUser");

        //case multiple matches exist
        User thirdUser = new User();
        thirdUser.setId(13L);
        Match otherMatch = userService.createMatch(testUser, thirdUser);
        otherMatch.setMatchId(223L);

        Mockito.when(matchRepository.countMatchByUserPair(thirdUser, testUser)).thenReturn(1);
      Mockito.when(matchRepository.countMatchByUserPair(testUser, thirdUser)).thenReturn(1);

      userService.setMatch(otherMatch);
        assertTrue(userService.doesMatchExist(testUser, thirdUser), "expected a match between testUser and thirdUser");
        assertTrue(userService.doesMatchExist(thirdUser,testUser), "expected a match between thirdUser and otherUser");
    }

    @Test
    void doesMatchExist_false(){
        // given
        User testUser2 = fillUser(3L, "testName", "testUsername3", "1234");
        testUser2.setGender(Gender.OTHER);

        Match testMatch = userService.createMatch(otherUser, testUser2);
        testMatch.setMatchId(2L);

        Mockito.when(matchRepository.findByMatchId(2L)).thenReturn(testMatch);
        userService.setMatch(testMatch);
        assertFalse(userService.doesMatchExist(testUser, otherUser));
        assertFalse(userService.doesMatchExist(otherUser, testUser));
    }

    @Test
    void checkGeneralAccess_granted(){
      testUser.setToken("token");
      Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(testUser);
      userService.checkGeneralAccess(testUser.getToken());

      // verifies that there is a call in this case there is one call to userRepository.findByToken(any parameter)
      Mockito.verify(userRepository, Mockito.times(1)).findByToken(Mockito.any());
    }

    @Test
    void checkGeneralAccess_denied(){
        testUser.setToken("token");
        Mockito.when(userRepository.findByToken(testUser.getToken())).thenReturn(null);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, ()-> userService.checkGeneralAccess("token"));
        assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
    }

    @Test
    void getCurrentBlackCards_emptyListReturned(){
      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
      assertEquals(Collections.emptyList(), userService.getCurrentBlackCards(testUser.getId()));
      testUser.setUserBlackCards(null);
      assertEquals(Collections.emptyList(), userService.getCurrentBlackCards(testUser.getId()));
      testUser.setUserBlackCards(new UserBlackCards());
      assertEquals(Collections.emptyList(), userService.getCurrentBlackCards(testUser.getId()));
      List<BlackCard> cards = new ArrayList<>();
      UserBlackCards ubc = new UserBlackCards();
      ubc.setBlackCards(cards);
      assertEquals(Collections.emptyList(), userService.getCurrentBlackCards(testUser.getId()));
      testUser.setUserBlackCards(userBlackCards);
      // ToDo: for 100% line coverage: test with Powermock to access private final Date of userBlackCards
      //assertEquals(Collections.emptyList(), userService.getCurrentBlackCards(testUser.getId()));
    }

    @Test
    void getCurrentBlackCards_blackCardReturned() {
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        testUser.setUserBlackCards(userBlackCards);
        List<BlackCard> expected = new ArrayList<>();
        expected.add(testBlackCard);
        List<BlackCard> actual = userService.getCurrentBlackCards(testUser.getId());
        assertEquals(expected, actual);
    }

    @Test
    void assignBlackCardsToUser_successSingleCard() {
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        List<BlackCard> cards = new ArrayList<>();
        cards.add(testBlackCard);
        userService.assignBlackCardsToUser(testUser.getId(), cards);
        UserBlackCards ubc = testUser.getUserBlackCards();
        assertArrayEquals(new List[]{cards}, new List[]{ubc.getBlackCards()});
    }

    @Test
    void assignBlackCardsToUser_successMultipleCards() {
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        List<BlackCard> cards = new ArrayList<>();
        cards.add(testBlackCard);
        BlackCard secondBlackCard = new BlackCard();
        cards.add(secondBlackCard);
        userService.assignBlackCardsToUser(testUser.getId(), cards);
        UserBlackCards ubc = testUser.getUserBlackCards();
        assertArrayEquals(new List[]{cards}, new List[]{ubc.getBlackCards()});
    }

    @Test
    void checkBlackCard_success_givenBlackCard() {
        Long id = testUser.getId();
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        testUser.setUserBlackCards(userBlackCards);
        assertDoesNotThrow(() -> userService.checkBlackCard(id, testBlackCard));
    }
    @Test
    void checkBlackCard_success_givenNoBlackCard() {
        Long id = testUser.getId();
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        testUser.setUserBlackCards(null);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.checkBlackCard(id, testBlackCard));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
        UserBlackCards ubc = new UserBlackCards();
        ubc.setBlackCards(null);
        testUser.setUserBlackCards(ubc);
        e = assertThrows(ResponseStatusException.class, () -> userService.checkBlackCard(id, testBlackCard));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
        List<BlackCard> cards = new ArrayList<>();
        ubc.setBlackCards(cards);
        testUser.setUserBlackCards(ubc);
        e = assertThrows(ResponseStatusException.class, () -> userService.checkBlackCard(id, testBlackCard));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }
    @Test
    void checkBlackCard_success_givenWrongBlackCard() {
        Long id = testUser.getId();
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        testUser.setUserBlackCards(userBlackCards);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.checkBlackCard(id, otherBlackCard));
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void hasUserAlreadyPlayInGame_true() {
        // case single Play
        Play play1 = new Play();
        play1.setUserId(otherUser.getId());
        testGame.enqueuePlay(play1);
        assertTrue(userService.hasUserAlreadyPlayInGame(testGame, play1));
        // case multiple Plays
        Play play2 = new Play();
        play2.setUserId(66L);
        assertFalse(userService.hasUserAlreadyPlayInGame(testGame, play2));
        testGame.enqueuePlay(play2);
        assertTrue(userService.hasUserAlreadyPlayInGame(testGame, play2));
    }

    @Test
    void hasUserAlreadyPlayInGame_false() {
        // case single Play
        Play play1 = new Play();
        play1.setUserId(33L);
        Play play2 = new Play();
        play2.setUserId(66L);
        Play play3 = new Play();
        play3.setUserId(99L);
        testGame.enqueuePlay(play1);
        assertFalse(userService.hasUserAlreadyPlayInGame(testGame, play2));
        // case multiple Plays
        testGame.enqueuePlay(play2);
        assertFalse(userService.hasUserAlreadyPlayInGame(testGame, play3));
    }

    @Test
    void getCurrentBlackCard_success() {

      Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);
      testUser.setActiveGame(testGame);

      BlackCard expected = testBlackCard;
      BlackCard actual = userService.getCurrentBlackCard(testUser.getId());
      assertEquals(expected, actual, "Expected the correct black to be returned");
    }

  @Test
  void getCurrentBlackCard_UserHasNoActiveGameOr_fail() {
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);
    Date pastDue = new Date(new Date().getTime() - Time.ONE_YEAR);
    testGame.setCreationTime(pastDue);
    Long id = testUser.getId();

    ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.getCurrentBlackCard(id));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  @Test
  void getCurrentBlackCard_UserHasNoBlackCardGame_fail() {

    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);
    testGame.setBlackCard(null);
    Long id = testUser.getId();

    ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.getCurrentBlackCard(id));
    assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
  }

  @Test
  void deleteUser_test(){
    assertDoesNotThrow(()-> userService.deleteUser(1L));
  }

  @Test
  void unmatch_success() {
    Match match = new Match();
    match.setMatchId(500);
    match.setUserPair(new Pair<>(testUser, otherUser));

    Mockito.when(userRepository.findById(otherUser.getId().longValue())).thenReturn(otherUser);
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);
    Mockito.when(matchRepository.countMatchByUserPair(testUser, otherUser)).thenReturn(1);
    Mockito.when(matchRepository.getMatchByUserPair(Mockito.any(), Mockito.any())).thenReturn(match);

    assertTrue(userService.doesMatchExist(testUser, otherUser));

    userService.deleteMatchBetweenUsers(testUser.getId(), otherUser.getId());

    Mockito.when(matchRepository.countMatchByUserPair(testUser, otherUser)).thenReturn(0);

    assertFalse(userService.doesMatchExist(testUser, otherUser));
  }

  @Test
  void unmatch_fail() {
    Match match = new Match();
    match.setMatchId(500);
    match.setUserPair(new Pair<>(testUser, otherUser));
    Long selfID = testUser.getId();
    Long otherID = otherUser.getId();

    Mockito.when(userRepository.findById(otherUser.getId().longValue())).thenReturn(otherUser);
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);
    Mockito.when(matchRepository.countMatchByUserPair(testUser, otherUser)).thenReturn(0);
    Mockito.when(matchRepository.getMatchByUserPair(Mockito.any(), Mockito.any())).thenReturn(match);

    assertFalse(userService.doesMatchExist(testUser, otherUser));

    ResponseStatusException e = assertThrows(ResponseStatusException.class,
            () -> userService.deleteMatchBetweenUsers(selfID, otherID));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void unmatch_fail_BIGERROR() {
    Match match = new Match();
    match.setMatchId(500);
    match.setUserPair(new Pair<>(testUser, otherUser));
    Long selfID = testUser.getId();
    Long otherID = otherUser.getId();

    Mockito.when(userRepository.findById(otherUser.getId().longValue())).thenReturn(otherUser);
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);
    Mockito.when(matchRepository.countMatchByUserPair(testUser, otherUser)).thenReturn(2);
    Mockito.when(matchRepository.getMatchByUserPair(Mockito.any(), Mockito.any())).thenReturn(match);

    ResponseStatusException e = assertThrows(ResponseStatusException.class,
            () -> userService.deleteMatchBetweenUsers(selfID, otherID));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
  }

  @Test
  void block_user_success() {
    Match match = new Match();
    match.setMatchId(500);
    match.setUserPair(new Pair<>(testUser, otherUser));

    Mockito.when(userRepository.findById(otherUser.getId().longValue())).thenReturn(otherUser);
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);
    Mockito.when(matchRepository.countMatchByUserPair(testUser, otherUser)).thenReturn(1);
    Mockito.when(matchRepository.getMatchByUserPair(Mockito.any(), Mockito.any())).thenReturn(match);


    assertTrue(userService.doesMatchExist(testUser, otherUser));

    userService.blockUser(testUser.getId(), otherUser.getId());

    Mockito.when(matchRepository.countMatchByUserPair(testUser, otherUser)).thenReturn(0);

    assertFalse(userService.doesMatchExist(testUser, otherUser));

    if (!testUser.getBlockedUsers().contains(otherUser)){
      fail("Expected otherUser to be in testUsers block list");
    }
    if (!otherUser.getBlockedUsers().contains(testUser)){
      fail("Expected testUser to be in otherUsers block list");
    }
  }

  @Test
  void agePreferenceCalculationsTest() {
      //Todo: create user with birthday 2nd Jan
      //Todo: create user with birthday 30th Dez
      testUser.setBirthday(new Date());
  }
}
