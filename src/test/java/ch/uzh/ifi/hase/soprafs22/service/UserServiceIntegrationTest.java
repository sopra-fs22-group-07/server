package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserBlackCardsRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
@TestPropertySource(
        locations = "application-integrationtest.properties")
class UserServiceIntegrationTest {

    private User testUser;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

    @Qualifier("userBlackCardsRepository")
    @Autowired
    private UserBlackCardsRepository userBlackCardsRepository;

    @Qualifier("MatchRepository")
    @Autowired
    private MatchRepository matchRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    testUser = new User();
    testUser.setUsername("username");
    testUser.setName("name");
    testUser.setPassword("password");
    testUser.setToken("1234");
    testUser.setGender(Gender.FEMALE);
    testUser.setBirthday(new Date());

    userRepository.deleteAll();
  }

  @AfterEach
  public void tearDown()  {
      userRepository.deleteAll();
  }

  @Test
  void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername(testUser.getUsername()));

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getName(), createdUser.getName());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(18, createdUser.getMinAge());
    assertEquals(3, createdUser.getGenderPreferences().size());
    assertTrue(createdUser.getGenderPreferences().contains(Gender.MALE));
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }

  @Test
  void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername(testUser.getUsername()));

    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setName("testName2");
    testUser2.setUsername(testUser.getUsername());
    testUser2.setStatus(UserStatus.ONLINE);
    testUser2.setToken("b");
    testUser2.setPassword("1234");
    testUser2.setBirthday(new Date());

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }

  @Test
  void getAllUsers_success() {
      User testUser2 = new User();
      testUser2.setUsername("username2");
      testUser2.setName("name2");
      testUser2.setPassword("password2");
      testUser2.setToken("12345");
      testUser2.setGender(Gender.FEMALE);
      testUser2.setBirthday(new Date());

      // given
      assertNull(userRepository.findByUsername(testUser.getUsername()));
      assertNull(userRepository.findByUsername(testUser2.getUsername()));

      userService.createUser(testUser);
      userService.createUser(testUser2);

      List<User> res = userService.getUsers();
      assertEquals(2, res.size());
      assertEquals(res.get(0).getId(), testUser.getId());
      assertEquals(res.get(1).getId(), testUser2.getId());
  }

  @Test
  void deleteUser_success() {
    assertNull(userRepository.findByUsername(testUser.getUsername()));

    userService.createUser(testUser);
    assertNotNull(userRepository.findByUsername(testUser.getUsername()));

    userService.deleteUser(testUser.getId());
    assertNull(userRepository.findByUsername(testUser.getUsername()));
  }
}
