package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.Like;

import javax.persistence.*;
import java.io.Serializable;

/**
 * class play which combines the played with card of a user with the user itself
 */

@Entity
@Table(name = "GAME")
public class Play implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private WhiteCard whiteCard;

    @Column
    private Long userId;

    @Column
    private Like liked = Like.NONE;

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

  public void setLiked(Like like){
      this.liked = like;
  }
}
