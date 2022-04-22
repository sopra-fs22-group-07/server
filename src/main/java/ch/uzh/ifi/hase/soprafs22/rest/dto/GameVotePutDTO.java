package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class GameVotePutDTO {

  private long userId;
  private boolean like;

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public boolean isLike() {
    return like;
  }

  public void setLike(boolean like) {
    this.like = like;
  }
}
