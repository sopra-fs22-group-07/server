package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This is the Match Entity, it saves two users into it, and has a unique ID. It receives and returns a Pair of users.
 */
@Entity
@Table(name = "Match")
public class Match implements Serializable {

  @Id
  @GeneratedValue
  private long matchId;

  @Column
  private Date creationDate = new Date();

  @OneToMany
  private List<User> userPair = new ArrayList<>();


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
    this.userPair.add(userPair.getObj1());
    this.userPair.add(userPair.getObj2());
  }

  public Pair<User, User> getUserPair() {
    return new Pair<>(userPair.get(0), userPair.get(1));
  }

}
