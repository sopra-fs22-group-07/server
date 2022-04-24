package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

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
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
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

}
