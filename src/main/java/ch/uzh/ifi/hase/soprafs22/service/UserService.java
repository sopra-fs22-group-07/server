package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  public User logoutUser(User user) {
    User userToBeLoggedOut = getUserById(user.getId());


    userToBeLoggedOut.setStatus(UserStatus.OFFLINE);
    return userToBeLoggedOut;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated- User that shall be saved to database
   * @throws org.springframework.web.server.ResponseStatusException: conflict
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    // ERROR 409: user already exists with Username
    String baseErrorMessage = "The %s provided %s not unique.";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              String.format(baseErrorMessage, "username", "is"));
    }
  }

  private void checkIfUserExistsForNewUsername(User user) {

    User userByUsername = userRepository.findByUsername(user.getUsername());

    if (userByUsername != null && !Objects.equals(userByUsername.getId(), user.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              "Username is already taken");
    }
  }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the password
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param inputUser user as input
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    public User checkPasswordAndUsername(User inputUser) {
        User userByUsername = userRepository.findByUsername(inputUser.getUsername());
        // test if user exists and correct password is given
        if (userByUsername == null || !Objects.equals(inputUser.getPassword(), userByUsername.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Your Username or password is incorrect");
        }
        userByUsername.setStatus(UserStatus.ONLINE);
        this.userRepository.save(userByUsername);
        return userByUsername;
    }

  public void checkGeneralAccess(String token) {
    User user = userRepository.findByToken(token);
    if(user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Log in or Register to access data! ");
    }
  }

  public User getUserById(long userId) {
    User user = this.userRepository.findById(userId);
    if(user == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user does not exit");
    }
    return user;
  }

  public void checkSpecificAccess(String token, long userId) {
    User userByToken = userRepository.findByToken(token);
    User userById = userRepository.findById(userId);
    if(userByToken == null || userByToken != userById) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You're not allowed to update this user! ");
    }
  }

  public User updateUser(User user) {

    // user has right id since we set it in the user-controller
    User userToBeUpdated = getUserById(user.getId()); // 404
    checkIfUserExistsForNewUsername(user); // 409

    // finally, update User in repository
    userToBeUpdated.setUsername(user.getUsername());
    userToBeUpdated.setBirthday(user.getBirthday());
    return userToBeUpdated;
  }

  public boolean isAvailable(User userInput) {
    User user = userRepository.findByUsername(userInput.getUsername());
    return user == null;
  }
}
