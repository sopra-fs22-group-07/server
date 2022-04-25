package ch.uzh.ifi.hase.soprafs22.rest.dto;

public class CardGetDTO {

  private long id;
  private String text;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
