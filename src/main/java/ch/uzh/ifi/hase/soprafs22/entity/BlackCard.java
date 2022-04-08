package ch.uzh.ifi.hase.soprafs22.entity;

public class BlackCard extends Card{
  public BlackCard(){
    super();
  }
  public BlackCard(long id, String text) {
    super(id, text);
  }
  public BlackCard(long id, String text, int nrOfBlanks) {
    super(id,text);
    this.nrOfBlanks = nrOfBlanks;
  }
  private int nrOfBlanks;

}
