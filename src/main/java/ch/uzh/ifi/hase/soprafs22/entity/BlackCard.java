package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "BLACKCARD")
public class BlackCard extends Card implements Serializable {

  @Column
  private int nrOfBlanks;

}
