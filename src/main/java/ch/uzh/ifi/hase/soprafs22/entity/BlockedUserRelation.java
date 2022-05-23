package ch.uzh.ifi.hase.soprafs22.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

/**
 * This Entity Class is used as a relationship class that holds the relationship if a user blocks another user. Basically,
 * it works exactly like Matches, therefore, if a user deletes his / her profile, the relationship gets deleted on
 * cascade. It creates two tables: One with the id and the creationDate and one with the relationship between the users.
 */
@Entity
@Table(name = "BLOCK_RELATION")
public class BlockedUserRelation implements Serializable {
  @Id
  @GeneratedValue
  @Column(name = "id")
  private long blockedRelationId;

  // In order that the block_relation table does not only hold the id ...
  @Column(name = "creation_date")
  private final Date creationDate = new Date();

  @ManyToMany(fetch = FetchType.LAZY,
          cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(name = "BLOCKED_USER_RELATION",
          joinColumns = {@JoinColumn(referencedColumnName = "id")},
          inverseJoinColumns = {@JoinColumn(referencedColumnName = "user_id")})
  private Set<User> users = new HashSet<>();

  public Pair<User, User> getUsers() {
    // convert Set to List
    return getUserPair(this.users);
  }

  /**
   * Because BlockedUserRelation holds two users, we can ask for the other user than the one given as parameter.
   * @param user: User that has a relationship with the other user
   * @return User: other User in the relationship (user != return value)
   */
  public User getBlockedUserFromUser(@NotNull User user) {
    Pair<User, User> userPair = getUserPair(this.users);
    return userPair.getObj1() == user ? userPair.getObj2() : userPair.getObj1();
  }

  public void setUserPair(@NotNull Pair<User, User> userPair) {
    this.users.add(userPair.getObj1());
    this.users.add(userPair.getObj2());
  }

  public long getBlockedRelationId() {
    return blockedRelationId;
  }

  public void setBlockedRelationId(long blockedRelationId) {
    this.blockedRelationId = blockedRelationId;
  }

  public Date getCreationDate() {
    return creationDate;
  }


  // extract code duplication
  static Pair<User, User> getUserPair(Set<User> users) {
    List<User> userList = new ArrayList<>(users);
    if (userList.size() != 2) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Got Relation with " + userList.size() + " users");
    }
    return new Pair<>(userList.get(0), userList.get(1));
  }
}
