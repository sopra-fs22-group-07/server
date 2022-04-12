package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.UserCards;

import java.util.*;

public class UserGetDetailsDTO {

  private Long id;
  private String name;
  private String username;
  private UserStatus status;
  private Date birthday;
  private Gender gender;
  private Date creationDate;
  private Game activeGame;
  private List<Game> pastGames;
  private UserCards userCards;
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

  public Game getActiveGame() {
    return activeGame;
  }

  public void setActiveGame(Game activeGame) {
    this.activeGame = activeGame;
  }

  public List<Game> getPastGames() {
    return pastGames;
  }

  public void setPastGames(List<Game> pastGames) {
    this.pastGames = pastGames;
  }

  public UserCards getUserCards() {
    return userCards;
  }

  public void setUserCards(UserCards userCards) {
    this.userCards = userCards;
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
}
