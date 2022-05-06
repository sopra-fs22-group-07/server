package ch.uzh.ifi.hase.soprafs22.rest.dto.chat;

import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;

public class ChatOverViewGetDTO {
  private UserGetDTO user;
  private Message message;

  public UserGetDTO getUser() {
    return user;
  }

  public void setUser(UserGetDTO user) {
    this.user = user;
  }

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;
  }
}
