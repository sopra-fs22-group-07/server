package ch.uzh.ifi.hase.soprafs22.entity;

/**
 * Internal Deck Representation
 * This class composes the internal representation of the Play and defines how
 * the Play is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 */


public class Play {
  // private long userId;
  private WhiteCard whiteCard;
  private User user;

  public WhiteCard getCard(){
    return  whiteCard;
  }

  public long getUserId(){
    return user.getId();
  }
}
