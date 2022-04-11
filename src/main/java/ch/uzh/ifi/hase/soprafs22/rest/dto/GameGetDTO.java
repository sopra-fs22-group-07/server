package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.entity.Play;

import java.util.List;

public class GameGetDTO {

    private long id;

    private List<Play>  plays;

    private Long userId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Play> getAllPlays(){
        return  this.plays;
    }
    public void setAllPlays(List<Play> plays){
        this.plays = plays;
    }

    public Long getUserId(){return userId;}
    public void setUserId(Long userId){this.userId = userId;}
}
