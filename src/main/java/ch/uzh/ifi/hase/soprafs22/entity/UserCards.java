package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "USERCARDS")
public class UserCards implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @OneToMany
  private List<BlackCard> blackCards = new ArrayList<>();

  @OneToMany
  private List<WhiteCard> whiteCards = new ArrayList<>();

  @Column
  private Date date = new Date();


  // GETTERS AND SETTERS

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public List<BlackCard> getBlackCards() {
    return blackCards;
  }

  public void setBlackCards(List<BlackCard> blackCards) {
    this.blackCards = blackCards;
  }

  public Date getDate() {
    return date;
  }


  public List<WhiteCard> getWhiteCards() {
    return whiteCards;
  }

  public void setWhiteCards(List<WhiteCard> whiteCards) {
    this.whiteCards = whiteCards;
  }

  public void removeWhiteCard(WhiteCard whiteCard) {
    this.whiteCards.remove(whiteCard);
  }
}
