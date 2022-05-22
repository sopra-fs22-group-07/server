package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.UserBlackCards;
import ch.uzh.ifi.hase.soprafs22.entity.WhiteCard;

import java.util.*;

public class UserGetDetailsDTO {

  private Long id;
  private String name;
  private String username;
  private UserStatus status;
  private Date birthday;
  private Gender gender;
  private Date creationDate;
  private List<Game> games = new ArrayList<>();
  private UserBlackCards userBlackCards;
  private List<WhiteCard> userWhiteCards;
  private Set<Long> likedByUsers;
  private Set<Long> matchIds;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }


  public Gender getGender(){
    return this.gender;
  }

  public void setGender(Gender gender){this.gender = gender; }

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

  public void setPastGames(List<Game> pastGames) {
    this.games.addAll(pastGames);
  }

  public Set<Long> getLikedByUsers() {
    return likedByUsers;
  }

  public void setLikedByUsers(Set<Long> likedByUsers) {
    this.likedByUsers = likedByUsers;
  }

  public Set<Long> getMatchIds() {
    return matchIds;
  }

  public void setMatchIds(Set<Long> matchIds) {
    this.matchIds = matchIds;
  }

  public UserBlackCards getUserBlackCards() {
    return userBlackCards;
  }

  public void setUserBlackCards(UserBlackCards userBlackCards) {
    this.userBlackCards = userBlackCards;
  }

  public List<WhiteCard> getUserWhiteCards() {
    return userWhiteCards;
  }

  public void setUserWhiteCards(List<WhiteCard> userWhiteCards) {
    this.userWhiteCards = userWhiteCards;
  }
}
