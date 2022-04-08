package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;

@Entity
@Table(name = "CARD")
public abstract class Card {

  public Card() {

  }
  public Card(long id, String text) {
    this.id = id;
    this.text = text;
  }

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String text;




  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
