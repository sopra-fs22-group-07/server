package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Internal Card Representation
 * This class composes the internal representation of the card and defines how
 * the card is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 */

@Entity
@Table(name = "CARD")
public abstract class Card implements Serializable{

  private static final long serialVersionUID = 1L;

  public Card(String text, String packName, int packID, boolean officialTag) {
    this.text = text;
    this.packName = packName;
    this.packID = packID;
    this.officialTag = officialTag;
  }

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false, unique = true)
  private String text;

  @Column(nullable = false)
  private String packName;

  @Column(nullable = false)
  private int packID;

  @Column(nullable = false)
  private boolean officialTag;

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

  public String getPackName() {
    return packName;
  }

  public void setPackName(String packName) {
    this.packName = packName;
  }

  public int getPackID() {
    return packID;
  }

  public void setPackID(int packID) {
    this.packID = packID;
  }

  public boolean isOfficialTag() {
    return officialTag;
  }

  public void setOfficialTag(boolean officialTag) {
    this.officialTag = officialTag;
  }
}
