package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Internal White Card Representation (extends Card)
 * This class composes the internal representation of the White card and defines how
 * the White card is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unique across the database -> composes
 * the primary key
 */

@Entity
public class WhiteCard extends Card implements Serializable {}
