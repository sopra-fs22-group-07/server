package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private MatchRepository matchRepository;

  @InjectMocks
  private UserService userService;



  private User testUser;

  private User otherUser;


    private WhiteCard testWhiteCard;
    private List<WhiteCard> testWhiteCards;

    private BlackCard testBlackCard;
    private Game testGame;

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

    //user for matching etcother
    otherUser = new User();
    otherUser.setId(1L);
    otherUser.setName("otherUser");
    otherUser.setUsername("testOtherUsername");
    otherUser.setPassword("1234");
    otherUser.setGender(Gender.OTHER);

    //give; white card for testing
    testWhiteCard = new WhiteCard();
    testWhiteCard.setId(1L);
    testWhiteCard.setText("CardText");
    testWhiteCards = new ArrayList<>();
    testWhiteCards.add(testWhiteCard);


    testGame = new Game();
    testBlackCard = new BlackCard();
    testBlackCard.setText("GapText");
    testBlackCard.setId(1L);
    testGame.setId(1L);
    testGame.setUserId(1L);
    testGame.setBlackCard(testBlackCard);
    testGame.setCreationTime(new Date());
    testGame.setGameStatus(GameStatus.ACTIVE);
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);

  }

  @Test
  void getUsers_empty(){
      List<User> emptyUserList = new ArrayList<>();
      assertEquals(emptyUserList, userService.getUsers());
  }

    @Test
    void getUsers_not_empty(){
        User user = userService.createUser(testUser);
        List<User> checkList = Arrays.asList(userService.createUser(testUser));
        List<User> userList = new ArrayList<>();
        userList.add(user);

        Mockito.when(userRepository.findAll()).thenReturn(checkList);

        assertEquals(userList, userService.getUsers());
    }

  @Test
  void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
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
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
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
      User inputUser = new User();
      inputUser.setId(1L);
      inputUser.setName("testName");
      inputUser.setUsername("testUsername");
      inputUser.setPassword("1234");

      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      // when -> setup additional mocks for UserRepository
      User returnUser = userService.checkPasswordAndUsername(inputUser);

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
      User inputUser = new User();
      inputUser.setId(1L);
      inputUser.setName("testName");
      inputUser.setUsername("testUsername");
      inputUser.setPassword("abcd");

      Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

      // then error, because different password
      assertThrows(ResponseStatusException.class, () -> userService.checkPasswordAndUsername(inputUser));
  }

  @Test
  void username_available_success() {
    String username = "Available Username";

    userService.createUser(testUser);
    User inputUser = new User();
    inputUser.setUsername("Available Username");

    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

    assertTrue(userService.isAvailable(username));
  }

  @Test
  void username_available_failure() {
    String username = testUser.getUsername();

    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    assertFalse(userService.isAvailable(username));
  }
  
    @Test
    void updateUser_success(){
      userService.createUser(testUser);
      User putUser = new User();
      putUser.setId(1L);
      putUser.setUsername("newUsername");
      putUser.setGender(Gender.MALE);

      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);

      User updatedUser = userService.updateUser(putUser);

      assertEquals("newUsername", updatedUser.getUsername());
      assertEquals(Gender.MALE, updatedUser.getGender());
  }

    @Test
    void updateUser_Conflict(){ //Test what happens when user tries to change username to a name that already is taken
        //Second user, that has the newUsername, that testUser wants
        User conflictUser = new User();
        conflictUser.setId(1L);
        conflictUser.setName("conflictName");
        conflictUser.setUsername("newUsername");
        conflictUser.setPassword("1234");
        conflictUser.setGender(Gender.OTHER);


        userService.createUser(testUser);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(conflictUser);

        User putUser = new User();
        putUser.setId(1L);
        putUser.setUsername("newUsername");
        putUser.setGender(Gender.MALE);

        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(putUser));
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

        //By default user shouldnt have an active gamee so should be true as no game added here
        assertEquals(true, userService.userHasNoActiveGame(id));
    }

    @Test
    void userHasNoActiveGame_false(){
        long id = testUser.getId();

        Mockito.when(userRepository.findById(id)).thenReturn(testUser);
        //Game added to user
        userService.addGame(id, testGame);
        assertEquals(false, userService.userHasNoActiveGame(id));
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
        testWhiteCard1.setId(2L);
        testWhiteCard1.setText("CardText2");
        WhiteCard testWhiteCard2 = new WhiteCard();
        testWhiteCard1.setId(3L);
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
      Game emptyGame = new Game();
      testUser.setActiveGame(emptyGame);
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
        //This moves the emptyGame to past Games
        testUser.flushGameToPastGames();
        userService.deleteGameIfEmpty(testUser, nonEmptyGame);
        //Should now not return empty list for past games
        assertNotEquals(Collections.emptyList(), testUser.getPastGames());
    }

}
