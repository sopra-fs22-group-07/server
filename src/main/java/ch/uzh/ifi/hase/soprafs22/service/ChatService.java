package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public ChatService( @Qualifier("ChatRepository") ChatRepository chatRepository,
                        @Qualifier("MessageRepository") MessageRepository messageRepository) {

        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Get the first messages of all chats, if chat is empty, add null
     * @param matches
     * @return
     */
    public List<Message> firstMessages(List<Match> matches){

        List<Message> firstMessages = new ArrayList<>();

        for(Match match : matches){
            // if no message, add null
            try{
            firstMessages.add(match.getChat().getMessages(0));
            }catch(ArrayIndexOutOfBoundsException e) {
                firstMessages.add(null);
            }
        }

        return firstMessages;

    }

    /**
     * Get all messages from a specific Chat
     * @param chatId: Id of chat
     * @return Message: all messages of chat
     */
    public List<Message> messagesFromChat(Long chatId, Long from, Long to) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat does not exit"));

        // from to (long) cast to int
        return  chat.getMessages(from.intValue(), to.intValue());

    }

    /**
     * Add Message to chat and save it in the message repository
     * @param chatId
     * @param message
     */
    public void addMessageToChat(Long chatId, Message message) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "chat does not exit")
        );

        chat.pushMessage(message);
        chatRepository.saveAndFlush(chat);
        messageRepository.saveAndFlush(message);
    }

}
