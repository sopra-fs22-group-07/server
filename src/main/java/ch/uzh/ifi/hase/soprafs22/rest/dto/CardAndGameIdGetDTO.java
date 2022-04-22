package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;

public class CardAndGameIdGetDTO {

  private BlackCard blackCard;
  private long gameId;

  public BlackCard getBlackCard() {
    return blackCard;
  }

  public void setBlackCard(BlackCard blackCard) {
    this.blackCard = blackCard;
  }

  public long getGameId() {
    return gameId;
  }

  public void setGameId(long gameId) {
    this.gameId = gameId;
  }
}
