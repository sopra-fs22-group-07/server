package ch.uzh.ifi.hase.soprafs22.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Game {

  private Long id;
  private LocalDate time;

  private List whiteCards = new ArrayList<WhiteCard>();

  public void enqueueWhiteCard(Play play){

  }
}
