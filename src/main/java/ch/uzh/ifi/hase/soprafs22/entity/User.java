package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 * - OneToMany: 1:n Relation with other entities
 * - OneToOne: 1:1 Relation with other entities
 */

@Entity
@Table(name = "USER")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private Date creationDate = new Date();

    @Column(nullable = false)
    private UserStatus status;

    @Column(length = 16, updatable = false)
    private byte[] salt = Passwords.getNextSalt();

    @Column(length = 64, nullable = false)
    private byte[] password;

    @Column
    private Date birthday;

    @Column
    private int minAge;

    @Column
    private int maxAge;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private int maxRange;

    @Column
    private Gender gender;

    @OneToMany(mappedBy = "user",
    cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Game> games = new ArrayList<>();

    // cascade must not include remove, so that white cards don't get deleted on user deletion
    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<WhiteCard> userWhiteCards = new ArrayList<>();

    @OneToOne
    private UserBlackCards userBlackCards;

    @ElementCollection
    private Set<Gender> genderPreferences = new TreeSet<>();

    @ElementCollection
    private Set<Long> likedByUsers = new TreeSet<>();

    // cascade must include remove, so that the matches are deleted as well (important)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "users")
    private Set<Match> matches = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "users")
    private Set<BlockedUserRelation> blockedUserRelations = new HashSet<>();


    // GETTERS AND SETTERS

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}

    public UserStatus getStatus() {return status;}
    public void setStatus(UserStatus status) {this.status = status;}

    public Date getCreationDate(){return this.creationDate; }
    public void setCreationDate(Date creationDate){this.creationDate = creationDate; }

    public Date getBirthday(){return this.birthday; }
    public void setBirthday(Date birthday){this.birthday = birthday; }

    public boolean getIsPasswordCorrect(String password){ return Passwords.isExpectedPassword(password.toCharArray(), this.salt, this.password); }
    public void setPassword(String password){ this.password = Passwords.hash(password.toCharArray(), this.salt); }

    public Gender getGender(){return this.gender; }
    public void setGender(Gender gender){this.gender = gender; }

    public Game getActiveGame() {
        if(!this.games.isEmpty() && this.games.get(games.size() - 1).getGameStatus()== GameStatus.ACTIVE){
            return  this.games.get(games.size() - 1);
        }
            return null;
    }

    public void addGame(Game game){
        this.games.add(game);
    }

    public void setActiveGame(Game activeGame) {
        activeGame.setGameStatus(GameStatus.ACTIVE);
        this.games.add(activeGame);}

    public List<Game> getPastGames() {
        if((!this.games.isEmpty()) &&
                (this.games.get(games.size() - 1).getGameStatus()== GameStatus.ACTIVE)){
            if(games.size()==1){
                return Collections.emptyList(); // empty list without active game
            }else{
                return games.subList(0, games.size() - 2); // list without active game
            }

        }
        return this.games;
    }

    public List<Game> getGames(){
        return this.games;
    }

    public Set<Match> getMatches() {return matches;}

    public List<WhiteCard> getUserWhiteCards() {return userWhiteCards;}
    public void setUserWhiteCards(List<WhiteCard> usersWhiteCards) {this.userWhiteCards = usersWhiteCards;}

    public UserBlackCards getUserBlackCards() {return userBlackCards;}
    public void setUserBlackCards(UserBlackCards userBlackCards) {this.userBlackCards = userBlackCards;}


    // HELPER FUNCTIONS

    /**
     * Method for Testing the gender of the user in JSON, as the gender should be
     * as string and not ENUM there
      * @return User.gender as string
     */
    public String getGenderString(){
        return this.gender.toString();
    }

    // move active game to past games
    public void flushGameToPastGames(){
        Game game = this.getActiveGame();
        if(game!=null){
            game.setGameStatus(GameStatus.INACTIVE);
        }
    }

    public void deletePastGame(Game game) {
        this.games.remove(game);
    }

    public void addMatch(Match match) {
        this.matches.add(match);
    }

    public void removeMatch(Match match) {
        this.matches.remove(match);
    }

    public Set<Long> getLikedByUsers() {
        return likedByUsers;
    }

    public void addLikeFromUser(Long userId){
        this.likedByUsers.add(userId);
    }

    public void removeLikeFromUser(User user){
        this.likedByUsers.remove(user.getId());
    }

    public boolean isLikedByUser(User user) {
        return this.likedByUsers.contains(user.getId());
    }


    public void removeWhiteCard(WhiteCard whiteCard) {
        this.userWhiteCards.remove(whiteCard);
    }

    public Set<Gender> getGenderPreferences(){return genderPreferences;}

    public void setGenderPreferences(Set<Gender> genderPreferences) {this.genderPreferences = genderPreferences;}

    public int getMinAge(){return minAge;}
    public void setMinAge(int minAge){this.minAge = minAge;}

    public int getMaxAge(){return maxAge;}
    public void setMaxAge(int maxAge){this.maxAge = maxAge;}

    public int getMaxRange(){return maxRange;}
    public void setMaxRange(int maxRange){this.maxRange = maxRange;}

    public Set<User> getMatchedUsers() {
        Set<User> matchedUsers = new HashSet<>();
        for (Match match : this.matches) {
            matchedUsers.add(match.getMatchedUserFromUser(this));
        }
        return matchedUsers;
    }

    public Set<User> getBlockedUsers() {
        Set<User> blockedUsers = new HashSet<>();
        for (BlockedUserRelation blockedUserRelation : this.blockedUserRelations) {
            blockedUsers.add(blockedUserRelation.getBlockedUserFromUser(this));
        }
        return blockedUsers;
    }

    public void addBlockedUsers(BlockedUserRelation blockedUserRelation) {
        this.blockedUserRelations.add(blockedUserRelation);
    }

    public double getLatitude() {return latitude;}
    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() {return longitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}
}
