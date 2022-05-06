package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String token;

    @Column
    private Date creationDate = new Date();

    // @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private String password;

    @Column
    private Date birthday;

    @Column
    private int minAge;

    @Column
    private int maxAge;

    @Column
    private Gender gender;

    @OneToOne
    private Game activeGame;

    @OneToMany
    private List<Game> pastGames = new ArrayList<>();

    @OneToMany
    private List<WhiteCard> userWhiteCards = new ArrayList<>();

    @OneToOne
    private UserBlackCards userBlackCards;

    @ElementCollection
    private Set<Gender> genderPreferences = new TreeSet<>();

    @ElementCollection
    private Set<Long> likedByUsers = new TreeSet<>();

    @ElementCollection
    private Set<Long> matches = new TreeSet<>();


    // GETTERS AND SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Date getCreationDate(){return this.creationDate; }

    public void setCreationDate(Date creationDate){this.creationDate = creationDate; }

    public Date getBirthday(){return this.birthday; }

    public void setBirthday(Date birthday){this.birthday = birthday; }

    public String getPassword(){return this.password; }

    public void setPassword(String password){this.password = password; }

    public Gender getGender(){return this.gender; }

    /**
     * Method for Testing the gender of the user in JSON, as the gender should be
     * as string and not ENUM there
      * @return User.gender as string
     */
    public String getGenderString(){
        return this.gender.toString();
    }

    public void setGender(Gender gender){this.gender = gender; }

    public void addGame(Game game){
        this.pastGames.add(game);
    }


    // move active game to past games
    public void flushGameToPastGames(){
        Game game = this.getActiveGame();
        this.setActiveGame(null);
        this.addGame(game);
    }

    public Game getActiveGame() {
        return activeGame;
    }

    public void setActiveGame(Game activeGame) {
        this.activeGame = activeGame;
    }

    public List<Game> getPastGames() {
        return pastGames;
    }

    public void deletePastGame(Game game) {
        this.pastGames.remove(game);
    }

    public Set<Long> getMatches() {
        return matches;
    }

    public void addMatch(Long matchId) {
        this.matches.add(matchId);
    }

    public void removeMatch(long matchId) {
        this.matches.remove(matchId);
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

    public List<WhiteCard> getUserWhiteCards() {
        return userWhiteCards;
    }

    public void setUserWhiteCards(List<WhiteCard> usersWhiteCards) {
        this.userWhiteCards = usersWhiteCards;
    }

    public void removeWhiteCard(WhiteCard whiteCard) {
        this.userWhiteCards.remove(whiteCard);
    }

    public UserBlackCards getUserBlackCards() {
        return userBlackCards;
    }

    public void setUserBlackCards(UserBlackCards userBlackCards) {
        this.userBlackCards = userBlackCards;
    }

    public Set<Gender> getGenderPreferences(){return genderPreferences;}

    public void setGenderPreferences(Set<Gender> genderPreferences) {this.genderPreferences = genderPreferences;}

    public int getMinAge(){return minAge;}

    public void setMinAge(int minAge){this.minAge = minAge;}

    public int getMaxAge(){return maxAge;}

    public void setMaxAge(int maxAge){this.maxAge = maxAge;}

}
