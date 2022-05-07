package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Not implemented by client, thus not documented in REST interface.
   */
  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers(@RequestHeader(value = "authorization", required = false) String token) {

    // check if source of query has access token
    userService.checkGeneralAccess(token); // 401, 404

    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseBody
  public ResponseEntity<UserGetDTO> createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput); // 409

    // set header
    MultiValueMap<String, String> headers = new HttpHeaders();
    headers.set("token", createdUser.getToken());

    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);

    return new ResponseEntity<>(userGetDTO, headers, HttpStatus.CREATED);
  }

    @PostMapping("/users/login")
    @ResponseBody
    public ResponseEntity<UserGetDTO> startSession(@RequestBody UserPostDTO userPostDTO) {
        // convert API user to internal representation convertUserPostDTOtoEntity
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check username and password, throws UNAUTHORIZED if false
        User returnUser = userService.doLogin(userInput); // 401

        // set header
        MultiValueMap<String, String> httpHeaders = new HttpHeaders();
        httpHeaders.set("token", returnUser.getToken());

        UserGetDTO returnUserDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(returnUser);

        // convert internal representation of user back to API
        return new ResponseEntity<>(returnUserDTO, httpHeaders, HttpStatus.OK);
    }

  @PutMapping("/users/logout/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void logoutUser(@RequestHeader(value = "authorization", required = false) String token,
                         @PathVariable(value = "userId") long userId
                        ){

    // check if caller is authorized
    userService.checkSpecificAccess(token, userId); // throws 401, 404

    User userInput = new User();
    // make sure user has right ID
    userInput.setId(userId);
    userInput.setToken(token);
    userService.logoutUser(userInput); // throws 404
  }

  @GetMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@RequestHeader(value = "authorization", required = false) String token,
                            @PathVariable(value = "userId") int userId) {
    userService.checkGeneralAccess(token);
    User user = userService.getUserById(userId);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  /**
   *Just additional, not really to be implemented by the Client, returns much more details about the user
   */
  @GetMapping("/users/{userId}/details")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDetailsDTO getUserInDetail(@RequestHeader(value = "authorization", required = false) String token,
                                           @PathVariable(value = "userId") int userId) {

    userService.checkSpecificAccess(token, userId);
    User user = userService.getUserById(userId);
    return DTOMapper.INSTANCE.convertEntityToUserGetDetailsDTO(user);
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(
          @RequestHeader(value = "authorization", required = false) String token,
          @PathVariable(value = "userId") long userId,
          @RequestBody UserPutDTO userPutDTO){

    userService.checkSpecificAccess(token, userId); // 401, 404
    User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
    // make sure user has right ID
    userInput.setId(userId);
    userService.updateUser(userInput); // 404, 409
  }

  @GetMapping("/users/usernames")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UsernameGetDTO checkUserNameAvailability(
          @RequestParam String username){

    // get the availability of the username
    boolean isAvailable = userService.isUsernameAvailable(username);

    // create and return ResponseEntity
    UsernameGetDTO responseBody = new UsernameGetDTO();
    responseBody.setAvailable(isAvailable);
    responseBody.setUsername(username);
    return responseBody;
  }

  @DeleteMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(
          @RequestHeader(value = "authorization", required = false) String token,
          @PathVariable(value = "userId") long userId){

      userService.checkSpecificAccess(token, userId); // 401, 404
      userService.deleteUser(userId);
  }

  @GetMapping("/users/{userId}/matches")
  @ResponseStatus(HttpStatus.OK)
  public List<UserGetDTO> getMatches(
          @RequestHeader(value = "authorization", required = false) String token,
          @PathVariable(value = "userId") long id){

      userService.checkSpecificAccess(token, id); // 401, 404
      List<User> matchedUsers = userService.getMatchedUsers(id);

      // create response object
      List<UserGetDTO> userGetDTOs = new ArrayList<>();
      for (User user : matchedUsers) {
          userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
      }

      return userGetDTOs;
  }


  //TODO: DELETE THIS MAPPING: I AM KEEPING IT IN HERE SO THE USERPAGE WORKS UNTIL THE NEW CHANGES ARE ALSO ADJUSTER IN THE CLIENT
    @GetMapping("/users/{userId}/preferences")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserPreferences(@RequestHeader(value = "authorization", required = false) String token,
                              @PathVariable(value = "userId") long userId) {
        userService.checkSpecificAccess(token, userId);
        User user = userService.getUserById(userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }//TODO: DELETE MAPPING ABOVE

    @PutMapping("/users/{userId}/preferences")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateUserPreferences(
            @RequestHeader(value = "authorization", required = false) String token,
            @PathVariable(value = "userId") long userId,
            @RequestBody UserPutDTO userPutDTO){

        userService.checkSpecificAccess(token, userId); // 401, 404
        User userPreferences = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        //userPreferences is just a user that only has the preferences and user id
        userPreferences.setId(userId);

        userService.updatePreferences(userPreferences);
    }


}
