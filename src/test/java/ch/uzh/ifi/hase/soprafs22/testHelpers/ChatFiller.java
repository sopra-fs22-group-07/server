package ch.uzh.ifi.hase.soprafs22.testHelpers;

import ch.uzh.ifi.hase.soprafs22.entity.*;

import java.util.List;

public class ChatFiller {
    protected User fillUser(Long userID, String token) {
        User user = new User();
        user.setId(userID);
        user.setToken(token);
        return user;
    }

    protected Message fillMessage(Long fromID, Long toID, String content, boolean read) {
        Message message = fillMessage(fromID, toID, content);
        message.setRead(read);
        return message;
    }
    protected Message fillMessage(Long fromID, Long toID, String content){
        Message message = new Message();
        message.setFromUserId(fromID);
        message.setToUserId(toID);
        message.setContent(content);
        return message;
    }

    protected Chat fillChat(long chatID, List<Message> messages) {
        Chat chat = new Chat();
        chat.setId(chatID);
        fillChat(chat, messages);
        return chat;
    }
    protected void fillChat(Chat chat, List<Message> messages) {
        for (Message message : messages) {
            chat.pushMessage(message);
        }
    }

    protected Match fillAndAddMatch(Long matchID, Pair<User, User> userPair, Chat chat) {
        Match match = new Match();
        match.setMatchId(matchID);
        match.setUserPair(userPair);
        match.setChat(chat);
        userPair.getObj1().addMatch(matchID);
        userPair.getObj2().addMatch(matchID);
        return match;
    }
}
