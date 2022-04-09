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


  // FIXME, TODO: for some reason we need nullable=true for black cards
  // otherwise it is impossible to add WHITE (!) cards to the repository.
  // I have no idea where this is coming from, but it is not a problem for
  // the black cards, as all have the nrOfBlanks set to some value.
  @Column(nullable = true)
  private int nrOfBlanks;

  public int getNrOfBlanks() {
    return nrOfBlanks;
  }

  public void setNrOfBlanks(int nrOfBlanks) {
    this.nrOfBlanks = nrOfBlanks;
  }

}
