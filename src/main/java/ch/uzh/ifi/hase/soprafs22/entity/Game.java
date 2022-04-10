package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Game Representation
 * This class composes the internal representation of the Game and defines how
 * the card is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 * - OneToMany: 1:n Relation with other entities
 */

@Entity
@Table(name = "GAME")
public class Game implements Serializable {

  public Game() {
      this.time = LocalDate.now();
  }

  @Id
  @GeneratedValue
  private Long id;

  @Column
  private Long userId;

  @Column
  private LocalDate time;

  @OneToOne
  private BlackCard blackCard;

  @OneToMany
  private List<Play> plays = new ArrayList<>();

  public void setUserId(long userId){
      this.userId=userId;
  }

  public Long getUserId(){
      return this.userId;
  }

  public void setBlackCard(BlackCard card){
        this.blackCard=card;
  }

  public BlackCard getBlackCard(){
        return this.blackCard;
  }

  public void enqueuePlay(Play play){
    plays.add(play);
  }

    public List<Play> getPlays() {
      return this.plays;
    }
}
