package ch.uzh.ifi.hase.soprafs22.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * This is the Match Entity, it saves two users into it, and has a unique ID. It receives and returns a Pair of users.
 */
@Entity
@Table(name = "MATCH")
public class Match implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "match_id")
  private long matchId;

  @Column
  private Date creationDate = new Date();

  @ManyToMany(fetch = FetchType.LAZY,
  cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(name = "USER_MATCHES",
  joinColumns = {@JoinColumn(referencedColumnName = "match_id")},
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

  public Pair<User, User> getUserPair() {
    // convert Set to List
    List<User> userList = new ArrayList<>(this.users);
    if (userList.size() != 2) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Got Match with " + userList.size() + " users");
    }
    return new Pair<>(userList.get(0), userList.get(1));
  }

  public Chat getChat(){ return  this.chat; }

  public void setChat(Chat chat){
      this.chat = chat;
  }

}
