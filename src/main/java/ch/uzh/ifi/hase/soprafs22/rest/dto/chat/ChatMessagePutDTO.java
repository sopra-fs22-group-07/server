package ch.uzh.ifi.hase.soprafs22.rest.dto.chat;

import ch.uzh.ifi.hase.soprafs22.constant.MessageType;

public class ChatMessagePutDTO {

  private MessageType messageType;
  private String content;

  public MessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = MessageType.valueOf(messageType);
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
