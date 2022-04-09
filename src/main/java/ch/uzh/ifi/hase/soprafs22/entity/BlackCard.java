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

  @Column
  private int nrOfBlanks;

}
