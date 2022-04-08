package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;

@Entity
@Table(name = "CARDS")
public class Cards {

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
