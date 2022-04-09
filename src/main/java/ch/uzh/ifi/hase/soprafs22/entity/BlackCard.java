package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Internal Black Card Representation (extends Card)
 * This class composes the internal representation of the black card and defines how
 * the black card is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 */

@Entity
@Table(name = "BLACKCARD")
public class BlackCard extends Card implements Serializable {

    public BlackCard(String text, String packName, int packID, boolean officialTag, int nrOfBlanks) {
      super(text, packName, packID, officialTag);
      this.nrOfBlanks = nrOfBlanks;
    }

    // @Column(nullable = false)
    // private int numPicks;

    // public int getNumPicks() {
    //     return numPicks;
    // }

    // public void setNumPicks(int numPicks) {
    //     this.numPicks = numPicks;
    // }

  @Column(nullable = false)
  private int nrOfBlanks;

  public int getNrOfBlanks() {
    return nrOfBlanks;
  }

  public void setNrOfBlanks(int nrOfBlanks) {
    this.nrOfBlanks = nrOfBlanks;
  }

}
