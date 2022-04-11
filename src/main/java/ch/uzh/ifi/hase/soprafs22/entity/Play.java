package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * class play which combines the played with card of a user with the user itself
 */

@Entity
@Table(name = "PLAY")
public class Play implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private WhiteCard whiteCard;

    @Column
    private Long userId;

  public WhiteCard getCard(){
    return  this.whiteCard;
  }
    public void setCard(WhiteCard card){
        this.whiteCard = card;
    }


  public long getUserId(){
    return userId;
  }

    public void setUserId(long userId){
        this.userId= userId;
    }

  public long getPlayId(){
        return this.id;
    }
}
