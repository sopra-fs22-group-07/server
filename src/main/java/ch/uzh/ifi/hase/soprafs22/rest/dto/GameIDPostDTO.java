package ch.uzh.ifi.hase.soprafs22.rest.dto;


/**
 * Used for call /users/{userId}/whiteCards{cardId}.
 */
public class GameIDPostDTO {

  private long gameId;

  public long getGameId() {
    return gameId;
  }

  public void setGameId(long gameId) {
    this.gameId = gameId;
  }

}
