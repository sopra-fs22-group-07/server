package ch.uzh.ifi.hase.soprafs22.rest.dto.chat;

import ch.uzh.ifi.hase.soprafs22.constant.MessageType;

import java.util.Date;

public class ChatMessageGetDTO {

  private long id;
  private MessageType messageType;
  private String content;
  private long from;
  private long to;
  private Date creationDate;
  private boolean read;



  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public long getFrom() {
    return from;
  }

  public void setFrom(long from) {
    this.from = from;
  }

  public long getTo() {
    return to;
  }

  public void setTo(long to) {
    this.to = to;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }
}
