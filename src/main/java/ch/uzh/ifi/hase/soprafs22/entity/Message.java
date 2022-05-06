package ch.uzh.ifi.hase.soprafs22.entity;

import ch.uzh.ifi.hase.soprafs22.constant.MessageType;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MESSAGE")
public class Message {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private MessageType messageType;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  // cannot call it 'from', that would trigger a syntax error in DDL
  private Long fromUserId;

  @Column(nullable = false)
  private Long toUserId;

  @Column
  private Date creationDate = new Date();

  @Column(nullable = false)
  private boolean read = false;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
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

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public boolean isRead() {
    return this.read;
  }

  public void setRead(boolean read){
    this.read = read;
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
