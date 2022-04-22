package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class UsernameGetDTO {

  private boolean isAvailable;
  private String username;

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean available) {
    isAvailable = available;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
