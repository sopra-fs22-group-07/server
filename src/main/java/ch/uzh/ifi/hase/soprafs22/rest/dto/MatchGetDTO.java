package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Match;

public class MatchGetDTO {

  private Match match;

  public Match getMatch() {
    return match;
  }

  public void setMatch(Match match) {
    this.match = match;
  }
}
