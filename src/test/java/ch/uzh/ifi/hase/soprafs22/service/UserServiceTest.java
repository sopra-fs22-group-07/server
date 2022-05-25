package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.Time;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.helper.Pair;
import ch.uzh.ifi.hase.soprafs22.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserBlackCardsRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
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
class UserServiceTest {

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
  private Match testMatch;
  private Match otherMatch;

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
      User returnUser = userService.doLogin(inputUser, "1234");

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
      ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.doLogin(inputUser, "1234"));
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
    void updatePreferences_sucess() {
        User user = userService.createUser(testUser);
        Mockito.when(userRepository.findById(1L)).thenReturn(user);

        User preferences = new User();
        preferences.setId(1L);
        assertDoesNotThrow(() -> userService.updatePreferences(preferences));
        assertEquals(18, user.getMinAge());

        preferences.setGenderPreferences(new HashSet<>(Collections.singleton(Gender.MALE)));
        userService.updatePreferences(preferences);
        assertEquals(18, user.getMinAge());
        assertEquals(1, user.getGenderPreferences().size());
        assertTrue(user.getGenderPreferences().contains(Gender.MALE));

        preferences.setMinAge(70);
        preferences.setMaxAge(75);
        userService.updatePreferences(preferences);
        assertEquals(70, user.getMinAge());
        assertEquals(75, user.getMaxAge());

        preferences.setMaxRange(33);
        userService.updatePreferences(preferences);
        assertEquals(33, user.getMaxRange());

        // these are out of bound and should not change anything
        preferences.setMaxRange(0);
        userService.updatePreferences(preferences);
        assertEquals(33, user.getMaxRange());

        preferences.setMaxRange(20010);
        userService.updatePreferences(preferences);
        assertEquals(33, user.getMaxRange());
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
        assertEquals(otherUser, isMatch.getMatchedUserFromUser(testUser));
        assertEquals(testUser, isMatch.getMatchedUserFromUser(otherUser));
        assertNotNull(isMatch.getChat());
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
      assertTrue(testUser.getMatches().contains(testMatch));
      assertTrue(otherUser.getMatches().contains(testMatch));
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

      userService.setMatch(testMatch);
        //both users should now have the testMatch, so it should exist
        assertTrue(userService.doesMatchExist(testUser, otherUser), "expected a match between testUser and otherUser");
        assertTrue(userService.doesMatchExist(otherUser, testUser), "expected a match between otherUser and testUser");

        //case multiple matches exist
        User thirdUser = new User();
        thirdUser.setId(13L);
        Match otherMatch = userService.createMatch(testUser, thirdUser);
        otherMatch.setMatchId(223L);

      userService.setMatch(otherMatch);
        assertTrue(userService.doesMatchExist(testUser, thirdUser), "expected a match between testUser and thirdUser");
        assertTrue(userService.doesMatchExist(thirdUser,testUser), "expected a match between thirdUser and otherUser");
    }

    @Test
    void doesMatchExist_false(){
        assertFalse(userService.doesMatchExist(testUser, otherUser));
        assertFalse(userService.doesMatchExist(otherUser, testUser));
    }

    @Test
    void doesMatchExist_error(){ // 2 matches returned
        Match testMatch =  userService.createMatch(testUser, otherUser);
        testMatch.setMatchId(222L);
        userService.setMatch(testMatch);
        assertTrue(userService.doesMatchExist(testUser, otherUser));
        Match otherMatch =  userService.createMatch(testUser, otherUser);
        otherMatch.setMatchId(223L);
        userService.setMatch(otherMatch);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.doesMatchExist(testUser, otherUser));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatus());
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
      // for 100% line coverage: test with Powermock to access private final Date of userBlackCards needed
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
    Match match = userService.createMatch(testUser, otherUser);
    match.setMatchId(500);
    userService.setMatch(match);

    Mockito.when(userRepository.findById(otherUser.getId().longValue())).thenReturn(otherUser);
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);

    assertTrue(userService.doesMatchExist(testUser, otherUser));

    userService.deleteMatchBetweenUsers(testUser.getId(), otherUser.getId());

    assertFalse(userService.doesMatchExist(testUser, otherUser));
  }

  @Test
  void unmatch_fail() {
    Long selfID = testUser.getId();
    Long otherID = otherUser.getId();

    Mockito.when(userRepository.findById(otherUser.getId().longValue())).thenReturn(otherUser);
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);

    assertFalse(userService.doesMatchExist(testUser, otherUser));

    ResponseStatusException e = assertThrows(ResponseStatusException.class,
            () -> userService.deleteMatchBetweenUsers(selfID, otherID));
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
  }

  @Test
  void unmatch_fail_BIGERROR() {
    Match testMatch =  userService.createMatch(testUser, otherUser);
    testMatch.setMatchId(222L);
    userService.setMatch(testMatch);
    assertTrue(userService.doesMatchExist(testUser, otherUser));
    Match otherMatch =  userService.createMatch(testUser, otherUser);
    otherMatch.setMatchId(223L);
    userService.setMatch(otherMatch);
    Long selfID = testUser.getId();
    Long otherID = otherUser.getId();

    Mockito.when(userRepository.findById(otherUser.getId().longValue())).thenReturn(otherUser);
    Mockito.when(userRepository.findById(testUser.getId().longValue())).thenReturn(testUser);

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

    testUser.addMatch(match);
    otherUser.addMatch(match);
    assertTrue(userService.doesMatchExist(testUser, otherUser));

    userService.blockUser(testUser.getId(), otherUser.getId());

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
      Calendar cal = Calendar.getInstance();
      cal.set(2000, Calendar.JANUARY, 1);
      Date firstJan = cal.getTime();
      testUser.setBirthday(firstJan);
      cal.set(1999, Calendar.DECEMBER, 31);
      Date thirty1Dez = cal.getTime();
      otherUser.setBirthday(thirty1Dez);
      cal.setTime(new Date());
      cal.add(Calendar.DAY_OF_MONTH, -1);
      cal.add(Calendar.YEAR, -21);
      Date yesterdayBirthday = cal.getTime();
      User thirdUser = fillUser(3L, "third", "thirdUser", "pw3");
      thirdUser.setBirthday(yesterdayBirthday);
      cal.add(Calendar.DAY_OF_MONTH, 2);
      Date tomorrowBirthday = cal.getTime();
      User fourthUser = fillUser(4L, "fourth", "fourthUser", "pw4");
      fourthUser.setBirthday(tomorrowBirthday);

      userService.createUser(testUser);
      userService.createUser(otherUser);
      userService.createUser(thirdUser);
      userService.createUser(fourthUser);

      Set<Gender> expectedGenders = new TreeSet<>();
      expectedGenders.add(Gender.MALE);
      expectedGenders.add(Gender.FEMALE);
      expectedGenders.add(Gender.OTHER);

      assertEquals(expectedGenders, testUser.getGenderPreferences());
      assertEquals(expectedGenders, otherUser.getGenderPreferences());

      assertEquals(18, thirdUser.getMinAge());
      assertEquals(18, fourthUser.getMinAge());
      assertTrue(testUser.getMinAge() > 18 && otherUser.getMinAge() > 18);
      cal.setTime(new Date());
      boolean exception = (cal.get(Calendar.MONTH) == Calendar.DECEMBER && cal.get(Calendar.DAY_OF_MONTH) == 31);
      assertTrue(exception || testUser.getMaxAge() == otherUser.getMaxAge());
      assertEquals(thirdUser.getMaxAge(), fourthUser.getMaxAge() + 1);
  }

  @Test
  void getMatches_success() {
      setupMatches(1);
      // test for set equality
      assertEquals(Set.of(testMatch), new HashSet<>(userService.getMatches(testUser)));

      //case multiple matches exist
      setupMatches(2);
      assertEquals(Set.of(testMatch, otherMatch), new HashSet<>(userService.getMatches(testUser)));
  }

    @Test
    void getMatches_null() {
        Mockito.when(matchRepository.getOne(Mockito.any())).thenReturn(null);
        assertEquals(Collections.emptyList(), userService.getMatches(testUser));
    }

    @Test
    void getUsersFromMatches_success() {
        assertEquals(new ArrayList<>(), userService.getUsersFromMatches(testUser));
        setupMatches(1);
        assertEquals(List.of(otherUser), userService.getUsersFromMatches(testUser));
        setupMatches(2);
        List<User> res = userService.getUsersFromMatches(testUser);
        assertEquals(2, res.size());
        assertTrue(res.contains(otherUser));
    }

    @Test
    void getMatchedUsers_success() {
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        assertEquals(new ArrayList<>(),userService.getMatchedUsers(testUser.getId()));
        setupMatches(1);
        assertEquals(List.of(otherUser), userService.getMatchedUsers(testUser.getId()));
        setupMatches(2);
        List<User> res = userService.getMatchedUsers(testUser.getId());
        assertEquals(2, res.size());
        assertTrue(res.contains(otherUser));
    }

    @Test
    void getActiveGame_success() {
      testUser.setActiveGame(testGame);
      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
      assertEquals(testGame, userService.getActiveGame(testUser.getId()));
    }

    @Test
    void getActiveGame_fail() {
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> userService.getActiveGame(1L));
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
        assertEquals("No active game", e.getReason());
    }

    @Test
    void getChatIds_success() {
        setupMatches(1);
        setupMatches(2);

        Chat chat1 = new Chat();
        chat1.setId(88);
        testMatch.setChat(chat1);
        Chat chat2 = new Chat();
        chat2.setId(89);
        otherMatch.setChat(chat2);

        assertEquals(Collections.emptyList(), userService.getChatIds(new ArrayList<>()));
        assertEquals(List.of(88L), userService.getChatIds(List.of(testMatch)));
        assertEquals(List.of(88L, 89L), userService.getChatIds(List.of(testMatch, otherMatch)));
    }

    @Test
    void deleteNotNeededPastGamesWithoutPlays_success() {
      assertDoesNotThrow(() -> userService.deleteNotNeededPastGamesWithoutPlays(testUser));

      Game empty = new Game();
      testUser.addGame(empty);

      Play play1 = new Play();
      play1.setCard(testWhiteCard);
      play1.setUserId(otherUser.getId());
      testGame.enqueuePlay(play1);
      testUser.addGame(testGame);

      Game active = new Game();
      active.setGameStatus(GameStatus.ACTIVE);
      testUser.addGame(active);

      assertEquals(3, testUser.getGames().size());
      userService.deleteNotNeededPastGamesWithoutPlays(testUser);
      assertEquals(2, testUser.getGames().size());
      assertTrue(testUser.getGames().contains(testGame));
      assertTrue(testUser.getGames().contains(active));
    }

    @Test
    void updateLocation_success() {
        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
        testUser.setLatitude(1.1);
        testUser.setLongitude(1.1);

        double lati = 36.5;
        double longi = 44.5;
        userService.updateLocation(testUser.getId(), lati, longi);
        assertEquals(lati, testUser.getLatitude());
        assertEquals(longi, testUser.getLongitude());
        userService.updateLocation(testUser.getId(), 0, 0);
        assertEquals(lati, testUser.getLatitude());
        assertEquals(longi, testUser.getLongitude());
        userService.updateLocation(testUser.getId(), lati, 0);
        assertEquals(lati, testUser.getLatitude());
        assertEquals(0, testUser.getLongitude());
    }

    @Test
    void getLoginStatus_success() {
        testUser.setToken("token");

        Mockito.when(userRepository.findById(1L)).thenReturn(testUser);

        testUser.setStatus(UserStatus.OFFLINE);
        assertEquals("offline", userService.getLoginStatus(testUser.getToken(), testUser.getId()));

        testUser.setStatus(UserStatus.ONLINE);
        assertEquals("online", userService.getLoginStatus(testUser.getToken(), testUser.getId()));
    }

    private User fillUser(Long id, String name, String userName, String password) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setUsername(userName);
        user.setPassword(password);
        return user;
    }

    private void setupMatches(int whichMatch){
        if (whichMatch == 1) {
            testMatch = userService.createMatch(testUser, otherUser);
            testMatch.setMatchId(222L);
            testMatch.setUserPair(new Pair<>(testUser, otherUser));
            userService.setMatch(testMatch);
            Mockito.when(matchRepository.getOne(testMatch.getMatchId())).thenReturn(testMatch);
            Mockito.when(matchRepository.findByMatchId(testMatch.getMatchId())).thenReturn(testMatch);
        } else if (whichMatch == 2) {
            User thirdUser = new User();
            thirdUser.setId(13L);
            otherMatch = userService.createMatch(thirdUser, testUser);
            otherMatch.setMatchId(223L);
            userService.setMatch(otherMatch);
            Mockito.when(matchRepository.getOne(otherMatch.getMatchId())).thenReturn(otherMatch);
            Mockito.when(matchRepository.findByMatchId(otherMatch.getMatchId())).thenReturn(otherMatch);
        }
    }
}
