package ch.uzh.ifi.hase.soprafs22.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "CHAT")
public class Chat {

  @Id
  @GeneratedValue
  private Long id;

  @OneToMany
  private List<Message> messages = new ArrayList<>();

  @ElementCollection
  private Set<Long> userIds = new HashSet<>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the messages specified by fromIndex and toIndex, does not raise an IndexOutOfBoundException
   * @param fromIndex: the index of the fromIndex-latest message
   * @param toIndex: the index of the toIndex-latest message
   * @return List of Message with size <= toIndex - fromIndex, possibly empty.
   */
  public List<Message> getMessages(int fromIndex, int toIndex) {
    // make sure that we don't throw an index error here
    // new messages are at head of list
    int size = messages.size();
    // trying to fetch nonexistent elements, just return empty list
    if (fromIndex >= size) {
      return Collections.emptyList();
    }
    // adjust toIndex for not triggering IndexOutOfBoundsException
    if (toIndex >= size) {
      toIndex = size - 1;
    }
    // now, fromIndex must be < size, and toIndex too.
    return messages.subList(fromIndex, toIndex);
  }
    /**
     * Gets one Message from the chat
     * @param index: the index of the message
     * @return Message: the Message on position index
     */
    public Message getMessages(int index) {

        return messages.get(index);
    }

    /**
     * Gets all teh messages
     * @return Message: List of all messages
     */
    public List<Message> getMessages() {
        // return all
        return messages;
    }

  public void pushMessage(Message message) {
    // add at head, makes calculation easier
    messages.add(0, message);
  }

  public Set<Long> getUserIds() {
    return userIds;
  }

  public void setUserIds(Set<Long> userIds) {
    this.userIds = userIds;
  }
}
