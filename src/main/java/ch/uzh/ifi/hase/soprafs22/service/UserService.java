package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.Time;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserBlackCardsRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


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
  private final UserBlackCardsRepository userBlackCardsRepository;
  private final MatchRepository matchRepository;
  private final GameService gameService;
  private boolean areInstantiatedDemoUsers = false;


  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository,
                     @Qualifier("userBlackCardsRepository") UserBlackCardsRepository userBlackCardsRepository,
                     @Qualifier("gameService") GameService gameService,
                     @Qualifier("MatchRepository") MatchRepository matchRepository) {
    this.userRepository = userRepository;
    this.userBlackCardsRepository = userBlackCardsRepository;
    this.matchRepository = matchRepository;
    this.gameService = gameService;
  }

    public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  /**
   * Creates and saves a new user
   * @param newUser: User that shall be created
   * @return user that was created
   */
  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setMinAge(findMinAgeDefault(newUser.getBirthday()));
    newUser.setMaxAge(findMaxAgeDefault(newUser.getBirthday()));
    Set<Gender> genderPreferences = new TreeSet<>();
    newUser.setAge(getAge(newUser.getBirthday()));
    genderPreferences.add(Gender.MALE);
    genderPreferences.add(Gender.FEMALE);
    genderPreferences.add(Gender.OTHER);
    newUser.setGenderPreferences(genderPreferences);

    checkIfUserExists(newUser);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

    private long getAgeInMilliSeconds(Date birthday) {
        return new Date().getTime() - birthday.getTime();
    }

    private int getAge(Date birthday) {
        int years;
        int months;
        Calendar birthDayMili = Calendar.getInstance();
        birthDayMili.setTimeInMillis(birthday.getTime());
        long currentTime = System.currentTimeMillis();
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(currentTime);
        years = now.get(Calendar.YEAR) - birthDayMili.get(Calendar.YEAR);
        int currMonth = now.get(Calendar.MONTH);
        int birthMonth = birthDayMili.get(Calendar.MONTH);
        months = currMonth - birthMonth;
        if (months < 0)
        {
            years--;
            months = 12 - birthMonth + currMonth;
            if (now.get(Calendar.DATE) < birthDayMili.get(Calendar.DATE))
                months--;
        } else if (months == 0 && now.get(Calendar.DATE) < birthDayMili.get(Calendar.DATE))
        {
            years--;
            months = 11;
        }
        if (months == 12) {
            years++;
        }
        return years;
    }

    private int findMinAgeDefault(Date userBirthday){
        long age = getAgeInMilliSeconds(userBirthday);
        return age - 3 * Time.ONE_YEAR < 18 * Time.ONE_YEAR ? 18 : getAge(userBirthday) - 3;
    }

    private int findMaxAgeDefault(Date userBirthday) {
        return getAge(userBirthday) + 3;
    }

  /**
   * logout the user, sets the status to offline
   * @param user: User to be logged out
   * @return : User that was logged out
   */
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

  /**
   * This is a method that checks if a username is available, but only raises an error if the user given in the
   * argument is different from the user retrieved from the userRepository.
   * @param user: user with username that is checked against.
   */
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
     * defined in the User entity. The method also sets the status of the user to ONLINE.
     *
     * @param inputUser user as input
     * @throws org.springframework.web.server.ResponseStatusException: 401
     * @see User
     */
    public User doLogin(User inputUser) {
        User userByUsername = userRepository.findByUsername(inputUser.getUsername());
        // test if user exists and correct password is given
        if (userByUsername == null || !Objects.equals(inputUser.getPassword(), userByUsername.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Your Username or password is incorrect");
        }
        userByUsername.setStatus(UserStatus.ONLINE);
        userByUsername.setAge(getAge(userByUsername.getBirthday())); //Updates Age of User whenever is logged in
        userRepository.saveAndFlush(userByUsername);
        return userByUsername;
    }

  /**
   * Check if the token belongs to any user, throws an Exception otherwise
   * @param token: String, token of a user
   */
  public void checkGeneralAccess(String token) {
    User user = userRepository.findByToken(token);
    if(user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Log in or Register to access data! ");
    }
  }

  /**
   * Gets a user by his userId, throws if there is no such user
   * @param userId: long
   * @return User retrieved
   */
  public User getUserById(long userId) {
    User user = this.userRepository.findById(userId);
    if(user == null){
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user does not exit");
    }
    return user;
  }

  /**
   * Check if the token belongs to User with userId, throws Exception if it does not.
   * @param token: String, token of a user
   * @param userId: long, id of a user
   */
  public void checkSpecificAccess(String token, long userId) {
    User userByToken = userRepository.findByToken(token);
    User userById = getUserById(userId); // 404
    if(userByToken == null || userByToken != userById) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You're not allowed to Access this user! ");
    }
  }

  /**
   * This method call updates a user
   * @param user: User to be updated
   * @return updated User
   */
  public User updateUser(User user) {
    // user has right id since we set it in the user-controller
    User userToBeUpdated = getUserById(user.getId()); // 404
    checkIfUserExistsForNewUsername(user); // 409

    // finally, update User in repository
    // check if the username is not just spaces
    if(!user.getUsername().equals("")){
        userToBeUpdated.setUsername(user.getUsername());
    }
    userToBeUpdated.setGender(user.getGender());
    return userToBeUpdated;
  }

  public void updatePreferences(User user){
      //Getting the correct user (404 and 409 should be check by specific access already)
      User userToUpdatePreferences = getUserById(user.getId());
      //Update User Preferences
      if(user.getMinAge()>= 18 && user.getMinAge() <= user.getMaxAge()){ //they can both be 22 for instance. If you only want people that are 22 years old
          userToUpdatePreferences.setMinAge(user.getMinAge());
          userToUpdatePreferences.setMaxAge(user.getMaxAge());
      }
      if(!Objects.isNull(user.getGenderPreferences()) && !user.getGenderPreferences().isEmpty()){
          userToUpdatePreferences.setGenderPreferences(user.getGenderPreferences());
      }
  }

  /**
   * Checks if a username is available
   * @param userInput: String
   * @return true if username is available, false otherwise
   */
  public boolean isUsernameAvailable(String userInput) {
    User user = userRepository.findByUsername(userInput);
    return user == null;
  }

  /**
   * Get White Cards from user
   * @param userId: userId from user
   * @return List of White Cards
   */
    public List<WhiteCard> getWhiteCards(Long userId) {
      return getUserById(userId).getUserWhiteCards();
    }

  /**
   * Add Game to User
   * @param userId: user to whom the game shall be added to
   * @param game: Game to be added
   */
    public void addGame(Long userId, Game game) {
        // get user
        User user = getUserById(userId);
        user.setActiveGame(game);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        userRepository.saveAndFlush(user);
    }

  /**
   * @param userId: userId
   * @return True if user has no active games, False otherwise
   */
  public Boolean userHasNoActiveGame(Long userId) {
    User user = getUserById(userId);
    return user.getActiveGame() == null;
  }

  /**
   * add a List of White Cards to user with userId
   * current white cards get overwritten, not added
   * @param userId: userId
   * @param whiteCards: list of white cards
   */
  public void assignWhiteCards(Long userId, List<WhiteCard> whiteCards) {
    // get the User
    User user = getUserById(userId);
    user.setUserWhiteCards(whiteCards);
    userRepository.saveAndFlush(user);

  }

  /**
   * Create a Match between two users. Also, it removes any likes from each others
   * @param user: User
   * @param otherUser: User
   * @return : created Match
   */
  public Match createMatch(User user, User otherUser) {

    // first, delete likes
    user.removeLikeFromUser(otherUser);
    otherUser.removeLikeFromUser(user);

    // then create a new match between users
    Match match = new Match();
    // Yes, it would be absolutely possible to do without the Pair class...
    Pair<User, User> pair = new Pair<>(user, otherUser);
    match.setUserPair(pair);
    matchRepository.saveAndFlush(match);

    return match;
  }

  /**
   * Deletes any game from User that is not active (which would be stupid to do)
   * @param user: User from whom the game shall be deleted from
   * @param game: Game which shall be deleted
   */
  public void deleteGameIfEmpty(User user, Game game) {
    // if game is not active and has no more plays, we can delete it.
    if(game.getPlays().isEmpty() && game.getGameStatus() != GameStatus.ACTIVE) {
      user.deletePastGame(game);
    }
    userRepository.saveAndFlush(user);
  }

  /**
   * Move Active Game to Past Games IF the time is up
   * @param userId: userId from User
   */
  public void updateActiveGameIfNecessary(Long userId) {
      updateActiveGameIfNecessary(getUserById(userId));
  }

  /**
   * Move Active Game to Past Games IF the time is up
   * @param user: User from whom the active games shall be updated
   */
  private void updateActiveGameIfNecessary(User user) {
    Game activeGame = user.getActiveGame();
    // case there is no active game, just return
    if (activeGame == null) return;

    // calculate how old the active game is
    long diffTime = new Date().getTime() - activeGame.getCreationTime().getTime();
    // case the game is older than one Day, put it to the past games
    if(diffTime > Time.ONE_DAY) {
      // update the user who was a candidate for black cards
      activeGame.setGameStatus(GameStatus.INACTIVE);
      user.flushGameToPastGames();
      // TODO: 12.04.2022 SaveAndFlush GameRepository here? (IDE doesn't complain until now)
      userRepository.saveAndFlush(user);
    }
    // case the active game is not older than 24 hours, just return
  }

  /**
   * Get the current black cards of a user: automatically delete old black cards that are older than some time period
   * @param userId: userId from the user from whom we want to get the current black cards
   * @return : a list of black cards - can be empty
   */
  public List<BlackCard> getCurrentBlackCards(Long userId) {
    User user = getUserById(userId);
    // get current black cards
    UserBlackCards userBlackCards = user.getUserBlackCards();
    if (userBlackCards == null || userBlackCards.getBlackCards() == null || userBlackCards.getBlackCards().isEmpty()) {
      return Collections.emptyList();
    }
    // check if cards are older than one day, return empty list if so, else return the cards (that are younger than one day)
    long diffTime = new Date().getTime() - userBlackCards.getBlackCardsTimer().getTime();
    if(diffTime > Time.ONE_DAY) {
      // update black cards
      user.setUserBlackCards(null);
      userRepository.saveAndFlush(user);
      return Collections.emptyList();
    }
    return userBlackCards.getBlackCards();
  }

  /**
   * Given a List of Black Cards, we want the user to hold these black cards for some time period.
   * Precondition: user.getCards() is null
   * @param userId: userId from user to whom the black cards shall be assigned
   * @param cards: List of black Cards
   */
  public void assignBlackCardsToUser(Long userId, List<BlackCard> cards) {
    User user = getUserById(userId);
    // make sure that user has no current black cards
    assert (user.getUserBlackCards() == null);

    // Create new instance of currentCards
    UserBlackCards userBlackCards = new UserBlackCards();
    userBlackCards.setBlackCards(cards);
    userBlackCardsRepository.saveAndFlush(userBlackCards);

    user.setUserBlackCards(userBlackCards);
    // save
    userRepository.saveAndFlush(user);
  }

  /**
   * Checks if Black Card has been assigned to User before calling this method.
   * @param userId: userId from User
   * @param blackCard: Black Card
   * @throws ResponseStatusException - 403 if black card is not valid for this user
   */
  public void checkBlackCard(Long userId, BlackCard blackCard) {
    // get user
    User user = getUserById(userId);
    // get current cards of user
    UserBlackCards usersBlackCards = user.getUserBlackCards();
    // case user hasn't been assigned black cards yet
    if (usersBlackCards == null || usersBlackCards.getBlackCards() == null || usersBlackCards.getBlackCards().isEmpty()) {
      String err = "caller hasn't fetched black cards to vote on. Call GET /users/"
              + userId + "/games to get black cards to select from";
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, err);
    }
    // case the card is not in the available cards for this user
    else if (!usersBlackCards.getBlackCards().contains(blackCard)) {
      String err = "you cannot select this card. Call GET /users/"
              + userId + "/games to get black cards to select from";
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, err);
    }
  }

  /**
   * Save a Match in the users tables
   * @param match: Match between two users
   */
  public void setMatch(Match match) {
    // Get Users
    Pair<User, User> userPair = match.getUserPair();
    User user1 = userPair.getObj1();
    User user2 = userPair.getObj2();

    // Add Match to both users
    user1.addMatch(match.getMatchId());
    user2.addMatch(match.getMatchId());

    // Save and Flush
    userRepository.save(user1);
    userRepository.save(user2);
    userRepository.flush();
  }

  /**
   * @param user User 1
   * @param otherUser User 2
   * @return boolean: true if User 2 has liked User 1, false otherwise
   */
  public boolean otherUserLikesUser(User user, User otherUser) {
      return user.isLikedByUser(otherUser);
  }

  /**
   * User likes another user, the other user saves that user likes him.
   * @param user: User
   * @param otherUser User
   */
  public void setUserLikesOtherUser(User user, User otherUser) {
    // add like
    otherUser.addLikeFromUser(user.getId());
    userRepository.saveAndFlush(otherUser);
  }

  /**
   * Determines if a game belongs to a user.
   * @param game: Game
   * @param user: user
   * @return boolean, true if game belongs to user
   */
  public boolean isGameBelongingToUser(Game game, User user) {
    return Objects.equals(game.getUserId(), user.getId());
  }

  /**
   * Determines if a White Card belongs to a user
   * @param card: White Card
   * @param userId: userId from User
   * @return boolean, True if white card belongs to user (for some time period)
   */
  public boolean isWhiteCardBelongingToUser(WhiteCard card, Long userId) {
    User user = getUserById(userId);
    List<WhiteCard> userWhiteCards = user.getUserWhiteCards();
    return !userWhiteCards.isEmpty() && userWhiteCards.contains(card);
  }

  /**
   * Determines if a Game has already a Play from the user who played the Game
   * @param game: Game
   * @param play: Play
   * @return boolean: true if user has already a play in the game
   */
  public boolean hasUserAlreadyPlayInGame(Game game, Play play) {
    long userId = play.getUserId();
    for(Play p : game.getPlays()) {
      if (p.getUserId() == userId){
        return true;
      }
    }
    return false;
  }

  /**
   * Delete a White Card from the current Cards of a Player
   * @param userId: userId
   * @param whiteCard: White Card
   */
  public void deleteWhiteCard(Long userId, WhiteCard whiteCard) {
    User user = getUserById(userId);
    user.removeWhiteCard(whiteCard);
    userRepository.saveAndFlush(user);
  }

  /**
   * Determine if a Match between two users already exists
   * @param user: User 1
   * @param otherUser: User 2
   * @return boolean: true if a Match already exists, false otherwise.
   */
  public boolean doesMatchExist(User user, User otherUser) {
    // it is enough to iterate through the matches of one user
    for(long matchId: user.getMatches()){
      Match match = matchRepository.findByMatchId(matchId);
      // get users from match
      Pair<User, User> userPair = match.getUserPair();
      // compare users with users from match
      if (userPair.equals(new Pair<>(user, otherUser))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Deletes a User from Repo By the USer id
   * @param userId: userId of a user
   */
  public void deleteUser(long userId){
      userRepository.deleteById(userId);
  }


  /**
   * get all users that user has matched with
   * @param userId: userId of user
   * @return list of users that matched with the user with id "userId"
   */
  // return list of users that matched with the user with id "userId"
  public List<User> getMatchedUsers(long userId) {

    // get id's of all matches of user with id userId
    Set<Long> matches = getUserById(userId).getMatches();

    // get user entities for all users with a match
    List<User> users = new ArrayList<>();

    // for all matches do:
    for (long matchId : matches) {
      Match match = matchRepository.findByMatchId(matchId);
      // get users from match and add to list the one that is not the user with id userId
      Pair<User, User> userPair = match.getUserPair();
      if (userPair.getObj1().getId() != userId) {
        users.add(userPair.getObj1());
      } else {
        users.add(userPair.getObj2());
      }
    }

    // return list of users
    return users;
  }


  // instantiate demo users
  public void instantiateDemoUsers() {

    if (areInstantiatedDemoUsers) {
      // throw exception if demo users are already instantiated
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demo users are already instantiated");

    } else {

      log.info("Instantiating demo users...");

      // ======= create demo users =======
      User demoUser1 = new User();
      demoUser1.setUsername("demoUser1");
      demoUser1.setPassword("demoUser1");
      demoUser1.setName("Demo User 1");
      demoUser1.setGender(Gender.MALE);
      demoUser1.setBirthday(new Date());
      demoUser1 = createUser(demoUser1);

      User demoUser2 = new User();
      demoUser2.setUsername("demoUser2");
      demoUser2.setPassword("demoUser2");
      demoUser2.setName("Demo User 2");
      demoUser2.setGender(Gender.FEMALE);
      demoUser2.setBirthday(new Date());
      demoUser2 = createUser(demoUser2);

      User demoUser3 = new User();
      demoUser3.setUsername("demoUser3");
      demoUser3.setPassword("demoUser3");
      demoUser3.setName("Demo User 3");
      demoUser3.setGender(Gender.OTHER);
      demoUser3.setBirthday(new Date());
      demoUser3 = createUser(demoUser3);


      // ======= create active games =======
      BlackCard blackCard1 = gameService.getNRandomBlackCards(1).get(0);
      BlackCard blackCard2 = gameService.getNRandomBlackCards(1).get(0);
      BlackCard blackCard3 = gameService.getNRandomBlackCards(1).get(0);
      Game game1 = gameService.createGame(blackCard1, demoUser1.getId());
      Game game2 = gameService.createGame(blackCard2, demoUser2.getId());
      Game game3 = gameService.createGame(blackCard3, demoUser3.getId());


      // ======= create likes and matches =======
      Match demoMatch1 = createMatch(demoUser1, demoUser2);
      setMatch(demoMatch1);


      Match demoMatch2 = createMatch(demoUser1, demoUser3);
      setMatch(demoMatch2);

      areInstantiatedDemoUsers = true;
      log.info("Demo users instantiated.");

    }
  }


  /**
   * Gets the black card of a user, but throws 404 if the user has no active game or no black card selected yet
   * @param userId: UserID of the user that we want the current black card of
   * @return blackCard (or 404)
   */
  public BlackCard getCurrentBlackCard(Long userId) {
    User user = getUserById(userId);
    // check if user has active game, or a black card chosen respectively
    if (user.getActiveGame() == null || user.getActiveGame().getBlackCard() == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No black card selected");
    }
    // else return the black card
    return user.getActiveGame().getBlackCard();
  }
}
