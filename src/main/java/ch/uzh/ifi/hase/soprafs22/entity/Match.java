package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

  @OneToOne
  private User user1;

  @OneToOne
  private User user2;


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
    this.user1 = userPair.getObj1();
    this.user2 = userPair.getObj2();
  }

  public Pair<User, User> getUserPair() {
    return new Pair<>(user1, user2);
  }

}
