package ch.uzh.ifi.hase.soprafs22.entity;

/**
 * class play which combines the played with card of a user with the user itself
 */

public class Play {

  private Long id;
  private WhiteCard whiteCard;
  private User user;

  public WhiteCard getCard(){
    return  this.whiteCard;
  }

  public long getUserId(){
    return user.getId();
  }

  public long getPlayId(){
        return this.id;
    }
}
