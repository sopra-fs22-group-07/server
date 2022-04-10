package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserPutDTO;
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

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers(@RequestHeader(value = "authorization", required = false) String token) {

    // check if source of query has access token
    userService.checkGeneralAccess(token);

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
    User createdUser = userService.createUser(userInput);

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
      User returnUser = userService.checkPasswordAndUsername(userInput);

      MultiValueMap<String, String> httpHeaders = new HttpHeaders();
      httpHeaders.set("token", returnUser.getToken());

      UserGetDTO returnUserDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(returnUser);

      // convert internal representation of user back to API
      return new ResponseEntity<>(returnUserDTO, httpHeaders, HttpStatus.OK);
    }

  @PutMapping("/users/logout/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void logoutUser(@RequestHeader(value = "authorization", required = false) String token,
                         @PathVariable(value = "id") long userId) {
    // check if caller is authorized
    userService.checkSpecificAccess(token, userId); // throws 401, 404

    User userInput = new User();
    // make sure user has right ID
    userInput.setId(userId);
    userInput.setToken(token);
    userService.logoutUser(userInput); // throws 404

  }

  @GetMapping("/users/{id}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@RequestHeader(value = "authorization", required = false) String token,
                            @PathVariable(value = "id") int userId) {

    userService.checkGeneralAccess(token);
    User user = userService.getUserById(userId);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PutMapping("/users/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(
          @RequestHeader(value = "authorization", required = false) String token,
          @PathVariable(value = "id") long userId,
          @RequestBody UserPutDTO userPutDTO){

    userService.checkSpecificAccess(token, userId); // throws 401, 404

    User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
    // make sure user has right ID
    userInput.setId(userId);
    userService.updateUser(userInput); // this throws all errors
  }
}
