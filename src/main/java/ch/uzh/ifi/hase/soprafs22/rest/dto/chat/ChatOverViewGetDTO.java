package ch.uzh.ifi.hase.soprafs22.rest.dto.chat;

import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.rest.dto.UserGetDTO;

import java.util.Date;

public class ChatOverViewGetDTO {
  private UserGetDTO user;
  private Message message;
  private Long chatId;
    private Date matchCreationDate;

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

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public Date getMatchCreationDate() {
        return matchCreationDate;
    }

    public void setMatchCreationDate(Date matchCreationDate) {
        this.matchCreationDate = matchCreationDate;
    }
}
