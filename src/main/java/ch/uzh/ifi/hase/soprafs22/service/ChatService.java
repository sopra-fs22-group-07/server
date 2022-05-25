package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Chat;
import ch.uzh.ifi.hase.soprafs22.entity.Match;
import ch.uzh.ifi.hase.soprafs22.entity.Message;
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

    private static final String NOT_EXISTS= "chat does not exist";

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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_EXISTS));

        // from to (long) cast to int
        return  chat.getMessages(from.intValue(), to.intValue());

    }

    /**
     * Add Message to chat and save it in the message repository
     * @param chatId: chat, where message gets added
     * @param message: massage to add
     */
    public Message addMessageToChat(Long chatId, Message message) {

        // First, create a proper message (with generated id!)
        Message msg = new Message();
        msg.setFromUserId(message.getFromUserId());
        msg.setContent(message.getContent());
        msg.setMessageType(message.getMessageType());
        msg.setToUserId(message.getToUserId());


        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_EXISTS)
        );

        chat.pushMessage(msg);
        messageRepository.saveAndFlush(msg);
        chatRepository.saveAndFlush(chat);
        return msg;
    }

    /**
     * Returns all unread messages of the chat
     * @param chatId: id of chat
     * @return List with all unread messages
     */
    public List<Message> getUnreadMessages(long chatId, long userId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_EXISTS)
        );

        List<Message> unreadMessages = new ArrayList<>();

        for(Message msg : chat.getMessages(0, chat.size())){
            // if no message, add null
            if(msg.isRead() && msg.getFromUserId()!=userId){
                break;
            }
            if(msg.getFromUserId()!=userId){
                unreadMessages.add(msg);
            }

        }
        return unreadMessages;
    }

    /**
     * Get the userId from the other user. Only gives the correct result, if there are already messages, because Chat
     * does not have references to the users. If there exists at least a message, the returned id is correct, else it is
     * null
     * @param chatId: long, id of the chat
     * @param userId: long, id of the caller of the function
     * @return Long, null if there are no messages between the users
     */
    public Long getUserIdFromOtherUser(long chatId, long userId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_EXISTS)
        );
        if (chat.getFirstMessage() != null) {
            Long u = chat.getFirstMessage().getFromUserId();
            return (u==userId) ? chat.getFirstMessage().getToUserId() : u;
        }
        return null;
    }

    /**
     * Get size of chat
     * @param chatId id of chat
     * @return returns size of chat as int
     */
    public int getSize(long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_EXISTS)
        );

        return chat.size();
    }

    /**
     * Set the messages of chat with chatID to read
     * @param chatId : wanted chat
     * @param userId : id of user, which read the chat and read the other users messages
     */
    public void setMessagesOfRead(long chatId, long userId) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, NOT_EXISTS)
        );

        for(Message msg : chat.getMessages(0, chat.size())){
            // if no message, add null
            if(msg.isRead() && msg.getFromUserId()!=userId){
                break;
            }
            if(msg.getFromUserId()!=userId){
                msg.setRead(true);
            }
        }
    }
}
