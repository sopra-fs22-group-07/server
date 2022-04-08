package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
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
 */

@Entity
@Table(name = "GAME")
public class Game {

  @Id
  @GeneratedValue
  private Long id;

  @Column
  private LocalDate time;

  @OneToOne
  private BlackCard blackCard;

  @OneToMany
  private ArrayList<WhiteCard> whiteCards;

  public void enqueueWhiteCard(Play play){
    whiteCards.add(play.getCard());
  }

  public BlackCard getBlackCard(){
    return this.blackCard;
  }
}
