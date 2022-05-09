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

    private final String notExists= "chat does not exist";

    @Autowired
    public ChatService( @Qualifier("ChatRepository") ChatRepository chatRepository,
                        @Qualifier("MessageRepository") MessageRepository messageRepository) {

        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Get the first messages of all chats, if chat is empty, add null
     * @param matches: list of matches
     * @return first message of the chats
     */
    public List<Message> getFirstMessages(List<Match> matches){

        List<Message> firstMessages = new ArrayList<>();

        for(Match match : matches){
            // if no message, add null
            firstMessages.add(match.getChat().getFirstMessage());
        }

        return firstMessages;

    }

    /**
     * Get all messages from a specific Chat
     * @param chatId: Id of chat
     * @return Message: all messages of chat
     */
    public List<Message> getMessagesFromChat(Long chatId, Long from, Long to) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notExists));

        // from to (long) cast to int
        return  chat.getMessages(from.intValue(), to.intValue());

    }

    /**
     * Add Message to chat and save it in the message repository
     * @param chatId: chat, where message gets added
     * @param message: massage to add
     */
    public void addMessageToChat(Long chatId, Message message) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notExists)
        );

        chat.pushMessage(message);
        chatRepository.saveAndFlush(chat);
        messageRepository.saveAndFlush(message);
    }

    /**
     * Returns all unread messages of the chat
     * @param chatId: id of chat
     * @return List with all unread messages
     */
    public List<Message> getUnreadMessages(long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notExists)
        );

        List<Message> unreadMessages = new ArrayList<>();

        for(Message msg : chat.getMessages(0, chat.size())){
            // if no message, add null
            if(msg.isRead()){
                break;
            }
            unreadMessages.add(msg);
        }
        return unreadMessages;
    }

    public int getSize(long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notExists)
        );

        return chat.size();
    }

    /**
     * Set the messages of chat with chatID to read
     * @param chatId: wanted chat
     */
    public void setMessagesOfRead(long chatId) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, notExists)
        );

        for(Message msg : chat.getMessages(0, chat.size())){
            // if no message, add null
            if(msg.isRead()){
                break;
            }
            msg.setRead(true);
        }
    }
}
