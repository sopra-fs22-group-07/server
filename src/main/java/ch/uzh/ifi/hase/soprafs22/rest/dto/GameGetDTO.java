package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.Play;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import java.util.Date;
import java.util.List;

public class GameGetDTO {

    private long gameId;
    private List<Play> plays;
    private User user;
    private BlackCard blackCard;
    private Date creationDate;
    private GameStatus gameStatus;


    public List<Play> getPlays(){
        return  this.plays;
    }
    public void setPlays(List<Play> plays){
        this.plays = plays;
    }

    public User getUser(){return user;}
    public void setUser(User user){this.user = user;}

    public BlackCard getBlackCard() {
        return blackCard;
    }

    public void setBlackCard(BlackCard blackCard) {
        this.blackCard = blackCard;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }
}
