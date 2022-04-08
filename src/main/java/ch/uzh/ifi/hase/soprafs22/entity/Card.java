package ch.uzh.ifi.hase.soprafs22.entity;

public abstract class Card {
  private long id;
  private String text;

  Card(long id, String text){
    this.id=id;
    this.text=text;
  }
  Card(){}

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
