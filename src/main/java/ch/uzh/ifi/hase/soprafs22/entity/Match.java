package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.helper.Pair;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

import static ch.uzh.ifi.hase.soprafs22.entity.BlockedUserRelation.getUserPair;

/**
 * This is the Match Entity, it saves two users into it, and has a unique ID. It receives and returns a Pair of users.
 */
@Entity
@Table(name = "match")
public class Match implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private long matchId;

  @Column
  private Date creationDate = new Date();

  @ManyToMany(fetch = FetchType.LAZY,
  cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(name = "USER_MATCHES",
  joinColumns = {@JoinColumn(referencedColumnName = "id")},
  inverseJoinColumns = {@JoinColumn(referencedColumnName = "user_id")})
  private Set<User> users = new HashSet<>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "chatId", referencedColumnName = "id")
  private Chat chat;

  // GETTERS AND SETTERS

  public long getMatchId() {
    return matchId;
  }

  public void setMatchId(long matchId) {
    this.matchId = matchId;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public void setUserPair(Pair<User, User> userPair) {
    this.users.add(userPair.getObj1());
    this.users.add(userPair.getObj2());
  }

  public Pair<User, User> getUsers() {
    // convert Set to List
    // defined in BlockedUserRelation.java
    return getUserPair(this.users);
  }

  public Chat getChat(){ return  this.chat; }

  public void setChat(Chat chat){
      this.chat = chat;
  }

  public User getMatchedUserFromUser(@NotNull User user) {
    Pair<User, User> userPair = getUserPair(this.users);
    return userPair.getObj1() == user ? userPair.getObj2() : userPair.getObj1();
  }
}
