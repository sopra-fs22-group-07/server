package ch.uzh.ifi.hase.soprafs22.entity;

public class Play {
  private long userId;
  private WhiteCard whiteCard;

  public Card getCard(){
    return  whiteCard;
  }

  public long getUserId(){
    return userId;
  }
}
