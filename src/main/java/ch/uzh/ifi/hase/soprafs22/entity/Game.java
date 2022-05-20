package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Game implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name="user_id")
  @JsonBackReference
  private User user;

  @Column
  @Enumerated(EnumType.STRING)
  private GameStatus gameStatus;

  @Column
  private Date creationTime = new Date();

  @OneToOne
  private BlackCard blackCard;

  @OneToMany
  private List<Play> plays = new ArrayList<>();

  // Getters and Setters

  public void setId(long id){this.id=id;}
  public Long getId(){return this.id;}

  public void setUser(User user){this.user=user;}
  public User getUser(){return this.user;}

  public void setBlackCard(BlackCard card){this.blackCard=card;}
  public BlackCard getBlackCard(){return this.blackCard;}

  public void enqueuePlay(Play play){plays.add(play);}
  public List<Play> getPlays() {return this.plays;}

  public Date getCreationTime() {return creationTime;}
  public void setCreationTime(Date creationTime) {this.creationTime = creationTime;}

  public void deletePlaysFrom(Long userId) {
    // safety net - assuming user has succeeded in inserting multiple plays into this game, we delete them all
    List<Play> toBeRemoved= new ArrayList<>();
    for(Play play : this.plays) {
      if(play.getUserId() == userId){
        toBeRemoved.add(play);
      }
    }
    this.plays.removeAll(toBeRemoved);
  }

  public GameStatus getGameStatus() {return gameStatus;}
  public void setGameStatus(GameStatus gameStatus) {this.gameStatus = gameStatus;}

}
