package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.WhiteCard;

public class PlayGetDTO {

    private long id;

    private WhiteCard whiteCard;

    private Long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public WhiteCard getCard(){
        return  this.whiteCard;
    }
    public void setCard(WhiteCard card){
        this.whiteCard = card;
    }

    public Long getUserId(){return userId;}
    public void setUserId(Long userId){this.userId = userId;}
}
