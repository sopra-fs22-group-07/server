package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.helper.Pair;
import ch.uzh.ifi.hase.soprafs22.rest.dto.LocationPutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import ch.uzh.ifi.hase.soprafs22.testHelpers.UserFiller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
class UserControllerTest extends UserFiller {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void test_post_users_returns_201() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setName("Test User");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setGender(Gender.MALE);
    user.setBirthday(new Date());
    user.setStatus(UserStatus.OFFLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("Test User");
    userPostDTO.setUsername("testUsername");
    userPostDTO.setPassword("password");
    userPostDTO.setGender("MALE");
    userPostDTO.setBirthday(new Date());

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.name", is(user.getName())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  void validUser_login() throws Exception {
      // given
      User user = fillUser(1L, "1", "testUsername", "password");
      user.setName("Test User");
      user.setStatus(UserStatus.ONLINE);

      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setName("Test User");
      userPostDTO.setUsername("testUsername");
      userPostDTO.setPassword("password");
      userPostDTO.setGender("MALE");
      userPostDTO.setBirthday(new Date());


      given(userService.doLogin(Mockito.any(), Mockito.any())).willReturn(user);

      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/users/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.name", is(user.getName())))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  void invalidUser_noLogin() throws Exception {
      // given
      UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setName("Test User");
      userPostDTO.setUsername("testUsername");
      userPostDTO.setPassword("password");
      userPostDTO.setGender("MALE");
      userPostDTO.setBirthday(new Date());

      given(userService.doLogin(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));


      // when/then -> do the request + validate the result
      MockHttpServletRequestBuilder postRequest = post("/users/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPostDTO));

      // then
      mockMvc.perform(postRequest)
              .andExpect(status().isUnauthorized());
  }

  @Test
  void test_post_users_returns_409() throws Exception {
    User user = new User();
    user.setUsername("John7");
    user.setPassword("password");

    UserPostDTO userPostDTO = new UserPostDTO();
      userPostDTO.setName("Test User");
      userPostDTO.setUsername("testUsername");
      userPostDTO.setPassword("password");
      userPostDTO.setGender("MALE");
      userPostDTO.setBirthday(new Date());

    given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO));

    mockMvc.perform(postRequest)
            .andExpect(status().isConflict());
  }


  @Test
  void test_get_users_ID_returns_200() throws Exception {
    // given
    User user = fillUser(1L, "69", "Firstname Lastname", "firstname@lastname", UserStatus.ONLINE, new Date(0));
    HashSet<Gender> genders = new HashSet<>();
    genders.add(Gender.FEMALE);
    user.setGenderPreferences(genders);
    user.setMaxAge(30);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUserById(user.getId())).willReturn(user);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/{id}", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("authorization", user.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.creationDate", is(parseDate(user.getCreationDate()))))
            .andExpect(jsonPath("$.birthday", is(parseDate(user.getBirthday()))))
            .andExpect(jsonPath("$.maxAge", is(30)))
            .andExpect(jsonPath("$.genderPreferences.[0]", is("FEMALE")));
  }


  @Test
  void test_get_users_ID_returns_404() throws Exception {
    // given
    User user = fillUser(1L, "69");

    given(userService.getUserById(user.getId())).willReturn(user);
    given(userService.getUserById(3)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    MockHttpServletRequestBuilder getRequest = get("/users/3")
            .contentType(MediaType.APPLICATION_JSON)
            .header("authorization", user.getToken());

    mockMvc.perform(getRequest).andExpect(status().isNotFound());
  }


  @Test
  void test_put_users_id_returns_204() throws Exception {
    User userToBeUpdated = fillUser(1L, "1", "usernameOld", "password", UserStatus.ONLINE, new Date());

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("usernameNew");
    userPutDTO.setBirthday(new Date(0));

    given(userService.getUserById(userToBeUpdated.getId())).willReturn(userToBeUpdated);

    MockHttpServletRequestBuilder putRequest = put("/users/{id}", userToBeUpdated.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPutDTO))
            .header("authorization", userToBeUpdated.getToken());

    mockMvc.perform(putRequest).andExpect(status().isNoContent());
    verify(userService).updateUser(any(User.class));
  }


  @Test
  void test_put_users_id_returns_404() throws Exception {

    // given
    User user = fillUser(1L, "69");

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("usernameNew");
    userPutDTO.setBirthday(new Date(0));

    given(userService.getUserById(user.getId())).willReturn(user);
    given(userService.updateUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(put("/users/{id}", 2L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPutDTO))
                    .header("authorization", user.getToken()))
            .andExpect(status().isNotFound());
  }



  @Test
  void test_put_users_id_returns_204_evenWithOldUsername() throws Exception {
    User userToBeUpdated = fillUser(1L, "1", "usernameOld", "password", UserStatus.ONLINE, null);

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername(userToBeUpdated.getUsername());
    userPutDTO.setBirthday(new Date(0));

    given(userService.getUserById(userToBeUpdated.getId())).willReturn(userToBeUpdated);
    MockHttpServletRequestBuilder putRequest = put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPutDTO))
            .header("authorization", userToBeUpdated.getToken());

    mockMvc.perform(putRequest).andExpect(status().isNoContent());
    verify(userService).updateUser(any(User.class));
  }


  @Test
  void test_put_users_id_returns_401() throws Exception {

    User user = fillUser(1L, "1");
    user.setUsername("username");
    user.setStatus(UserStatus.ONLINE);

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("usernameNew");
    userPutDTO.setBirthday(new Date(0));

    given(userService.updateUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    mockMvc.perform(put("/users/{id}", 2L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPutDTO)))
            .andExpect(status().isUnauthorized());
  }

  @Test
  void test_put_users_id_returns_409() throws Exception {
    User user = fillUser(1L, "1");
    user.setUsername("username");
    user.setStatus(UserStatus.ONLINE);

    User user2 = fillUser(2L, "2");

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername(user.getUsername());
    userPutDTO.setBirthday(new Date(0));

    given(userService.updateUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    mockMvc.perform(put("/users/{id}", 2L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPutDTO))
                    .header("authorization", user2.getToken()))
            .andExpect(status().isConflict());
  }


  @Test
  void test_post_users_login_returns_200() throws Exception {
    User user = fillUser(1L, "1", "username", "password", UserStatus.ONLINE, new Date(0));
    user.setGender(Gender.OTHER);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setUsername(user.getUsername());
    userPostDTO.setPassword("password");
    userPostDTO.setGender("OTHER");

    given(userService.doLogin(Mockito.any(), Mockito.any())).willReturn(user);

    mockMvc.perform(post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPostDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.status", is(user.getStatus().toString())))
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.creationDate", is(parseDate(user.getCreationDate()))))
            .andExpect(jsonPath("$.birthday", is(parseDate(user.getBirthday()))))
            .andExpect(jsonPath("$.gender", is(user.getGenderString())));
  }


  @Test
  void test_post_users_login_returns_404() throws Exception {
    UserPostDTO userPostDTO = new UserPostDTO();userPostDTO.setName("Test User");
      userPostDTO.setUsername("testUsername");
      userPostDTO.setPassword("wrongPassword");
      userPostDTO.setGender("MALE");
      userPostDTO.setBirthday(new Date());

    given(userService.doLogin(Mockito.any(), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(post("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPostDTO)))
            .andExpect(status().isNotFound());
  }

  @Test
  void test_users_logout_id_returns_204() throws Exception {
    User user = fillUser(1L, "1", "username", "password", UserStatus.ONLINE, new Date(0));

    UserPutDTO userPutDTO = new UserPutDTO();

    given(userService.logoutUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NO_CONTENT));

    mockMvc.perform(put("/users/logout/{id}", user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPutDTO))
                    .header("authorization", user.getToken()))
            .andExpect(status().isNoContent());
    verify(userService).logoutUser(any(User.class));
  }


  @Test
  void test_users_logout_id_returns_404() throws Exception {
    User user = fillUser(1L, "1", "username", "password", UserStatus.ONLINE, new Date(0));

    UserPutDTO userPutDTO = new UserPutDTO();

    given(userService.logoutUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(put("/users/logout/{id}", user.getId() + 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPutDTO))
                    .header("authorization", user.getToken()))
            .andExpect(status().isNotFound());
  }


  @Test
  void test_users_logout_id_returns_401() throws Exception {
    User user = fillUser(1L, "1", "username", "password", UserStatus.ONLINE, new Date(0));

    UserPutDTO userPutDTO = new UserPutDTO();

    given(userService.logoutUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

    mockMvc.perform(put("/users/logout/{id}", user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(userPutDTO))
                    .header("authorization", "wrong token"))
            .andExpect(status().isUnauthorized());
  }

  @Test
  void test_check_username_username_is_available() throws Exception {

    given(userService.isUsernameAvailable(Mockito.any())).willReturn(true);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users/usernames?username=available")
            .contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available", is(true)))
            .andExpect(jsonPath("$.username", is("available")));

  }

  @Test
  void test_check_username_username_is_not_available() throws Exception {

  given(userService.isUsernameAvailable(Mockito.any())).willReturn(false);

    mockMvc.perform(get("/users/usernames?username=taken")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.available", is(false)))
            .andExpect(jsonPath("$.username", is("taken")));
  }

  @Test
  void test_unmatch_user_success() throws Exception {
    User user = fillUser(1L, "token");
      User otherUser = fillUser(2L, "token2");

    Match match = new Match();
    match.setUserPair(new Pair<>(user, otherUser));

    mockMvc.perform(delete("/users/" + user.getId()+ "/matches/"+otherUser.getId()).contentType(MediaType.APPLICATION_JSON)
            .header("authorization", user.getToken()))
            .andExpect(status().isNoContent());
    verify(userService).deleteMatchBetweenUsers(user.getId(), otherUser.getId());
  }

  @Test
  void test_unmatch_user_no_match() throws Exception {
    User user = fillUser(1L, "token");
    User otherUser = fillUser(2L, "token2");

    Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).deleteMatchBetweenUsers(Mockito.anyLong(), Mockito.anyLong());

    mockMvc.perform(delete("/users/" + user.getId()+ "/matches/"+otherUser.getId()).contentType(MediaType.APPLICATION_JSON)
            .header("authorization", user.getToken()))
            .andExpect(status().isNotFound());
  }

  @Test
  void test_block_user_success() throws Exception {
    User user = fillUser(1L, "token");
    User otherUser = fillUser(2L, "token2");

    Match match = new Match();
    match.setUserPair(new Pair<>(user, otherUser));

    mockMvc.perform(put("/users/"+ user.getId()+"/matches/"+otherUser.getId()+"/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("authorization", user.getToken()))
            .andExpect(status().isNoContent());
    verify(userService).blockUser(user.getId(), otherUser.getId());
  }

  @Test
  void test_block_user_no_match() throws Exception {
    User user = fillUser(1L, "token");
    User otherUser = fillUser(2L, "token2");

    Mockito.doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST)).when(userService).blockUser(Mockito.anyLong(), Mockito.anyLong());

    mockMvc.perform(put("/users/"+ user.getId()+"/matches/"+otherUser.getId()+"/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("authorization", user.getToken()))
            .andExpect(status().isBadRequest());
  }

  @Test
  void getUserInDetail_success() throws Exception{
      User user = fillUser(2L, "2","Uname", "pw", UserStatus.ONLINE, new Date());
      WhiteCard whiteCard = new WhiteCard();
      whiteCard.setText("white1");
      user.setUserWhiteCards(List.of(whiteCard, new WhiteCard()));
      Game testGame = new Game();
      testGame.setId(33L);
      testGame.setUser(user);
      testGame.setBlackCard(new BlackCard());
      user.setActiveGame(testGame);

      doNothing().when(userService).checkSpecificAccess(user.getToken(), user.getId());
      given(userService.getUserById(user.getId())).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get("/users/{userId}/details", user.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .header("authorization", user.getToken());
      mockMvc.perform(getRequest).andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.userWhiteCards.[0].text", is(whiteCard.getText())))
              .andExpect(jsonPath("$.games.[0].id", is(testGame.getId().intValue())))
              .andExpect(jsonPath("$.games.[0].gameStatus", is("ACTIVE")));
  }

  @Test
  void getUserInDetail_fail() throws Exception {
    User user = fillUser(2L, "2","Uname", "pw", UserStatus.ONLINE, new Date());
    doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(userService).checkSpecificAccess(user.getToken(), user.getId());
    MockHttpServletRequestBuilder getRequest = get("/users/{userId}/details", user.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .header("authorization", user.getToken());
    mockMvc.perform(getRequest).andExpect(status().isUnauthorized());
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).checkSpecificAccess(user.getToken(), user.getId());
    mockMvc.perform(getRequest).andExpect(status().isNotFound());
    doNothing().when(userService).checkSpecificAccess(user.getToken(), user.getId());
    given(userService.getUserById(user.getId())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
    mockMvc.perform(getRequest).andExpect(status().isNotFound());
  }

  @Test
  void deleteUser_success() throws Exception {
      User user = fillUser(3L, "3");
      doNothing().when(userService).checkSpecificAccess(user.getToken(), user.getId());
      doNothing().when(userService).deleteUser(user.getId());
      MockHttpServletRequestBuilder deleteRequest = delete("/users/{userId}", user.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .header("authorization", user.getToken());
      mockMvc.perform(deleteRequest).andExpect(status().isNoContent());
      verify(userService).deleteUser(user.getId());
  }

    @Test
    void deleteUser_fail() throws Exception {
        User user = fillUser(3L, "3");
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).checkSpecificAccess(user.getToken(), user.getId());
        MockHttpServletRequestBuilder deleteRequest = delete("/users/{userId}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user.getToken());
        mockMvc.perform(deleteRequest).andExpect(status().isNotFound());
    }

    @Test
    void updateUserPreferences_success() throws Exception {
        User user1 = fillUser(1L, "1");
        HashSet<Gender> genders = new HashSet<>();
        genders.add(Gender.MALE);
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("1");
        userPutDTO.setGenderPreferences(genders);
        userPutDTO.setMaxAge(30);
        doNothing().when(userService).checkSpecificAccess(user1.getToken(), user1.getId());
        doNothing().when(userService).updatePreferences(any(User.class));
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/preferences", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("authorization", user1.getToken());
        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(userService).updatePreferences(any(User.class));
    }

    @Test
    void getLoginStatus_success() throws Exception {
        User user1 = fillUser(1L, "1");
        doNothing().when(userService).checkSpecificAccess(user1.getToken(), user1.getId());

        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/loginStatus", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", "1");

        user1.setStatus(UserStatus.OFFLINE);
        mockMvc.perform(getRequest).andExpect(status().isOk());

        user1.setStatus(UserStatus.ONLINE);
        mockMvc.perform(getRequest).andExpect(status().isOk());
        //TODO: read the returned value
    }

    @Test
    void updateLocation_success() throws Exception {
        User user1 = fillUser(1L, "1");
        doNothing().when(userService).checkSpecificAccess(user1.getToken(), user1.getId());

        LocationPutDTO locationDTO = new LocationPutDTO();
        locationDTO.setLatitude(16.6);
        locationDTO.setLongitude(18.8);

        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/location", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(locationDTO))
                .header("authorization", user1.getToken());
        mockMvc.perform(putRequest).andExpect(status().isNoContent());
        verify(userService).updateLocation(user1.getId(), 16.6, 18.8);
    }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object: Object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }

  private String parseDate(Date date) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    date.setHours(date.getHours() + date.getTimezoneOffset()/60);
    String s = dateFormat.format(date);
    return s + "+00:00";
  }
}