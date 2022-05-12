package ch.uzh.ifi.hase.soprafs22.rest.dto.chat;

import ch.uzh.ifi.hase.soprafs22.constant.MessageType;

public class ChatMessagePutDTO {

  private MessageType messageType;
  private String content;
  private Long fromUserId;
  private Long toUserId;

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

  public Long getFromUserId() {
    return fromUserId;
  }

  public void setFromUserId(Long fromUserId) {
    this.fromUserId = fromUserId;
  }

  public Long getToUserId() {
    return toUserId;
  }

  public void setToUserId(Long toUserId) {
    this.toUserId = toUserId;
  }
}
