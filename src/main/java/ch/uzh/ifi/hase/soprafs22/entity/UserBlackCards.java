package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERBLACKCARDS")
public class UserBlackCards implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  @ManyToMany
  private List<BlackCard> blackCards;

  @Column
  private final Date blackCardsTimer = new Date();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getBlackCardsTimer() {
    return blackCardsTimer;
  }

  public List<BlackCard> getBlackCards() {
    return blackCards;
  }

  public void setBlackCards(List<BlackCard> blackCards) {
    this.blackCards = blackCards;
  }
}
