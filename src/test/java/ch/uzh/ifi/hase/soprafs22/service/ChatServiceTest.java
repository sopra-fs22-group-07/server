package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.helper.Pair;
import ch.uzh.ifi.hase.soprafs22.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs22.testHelpers.ChatFiller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class ChatServiceTest extends ChatFiller {
    User user1;

    Message message2;
    Message message4;
    Message msg1unread;
    Message msg2unread;
    Message msg3unread;
    Chat chat1;

    List<Match> matches;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        user1 = fillUser(1L, "1one");
        User user2 = fillUser(2L, "2two");
        User user3 = fillUser(3L, "3three");
        Pair<User, User> userPair12 = new Pair<>(user1, user2);
        Pair<User, User> userPair13 = new Pair<>(user1, user3);
        Message message1 = fillMessage(1L, 2L, "first");
        message2 = fillMessage(2L, 1L, "second", true);
        Message message3 = fillMessage(3L, 1L, "third");
        message4 = fillMessage(1L, 3L, "fourth");
        msg1unread = fillMessage(user2.getId(), user1.getId(), "Hi", false);
        msg2unread = fillMessage(user1.getId(), user2.getId(), "Hey", false);
        msg3unread = fillMessage(user2.getId(), user1.getId(), "Bye", false);
        chat1 = fillChat(40, List.of(message1, message2));
        Chat chat2 = fillChat(41, List.of(message3, message4));
        Match match1 = fillAndAddMatch(30L, userPair12, chat1);
        Match match2 = fillAndAddMatch(31L, userPair13, chat2);
        matches = new ArrayList<>();
        matches.add(match1);
        matches.add(match2);
    }

    @Test
    void getFirstMessages() {
        List<Message> res = chatService.getFirstMessages(matches);
        assertArrayEquals(res.toArray(), List.of(message2, message4).toArray());
    }

    @Test
    void getMessagesFromChat_success() {
        given(chatRepository.findById(chat1.getId())).willReturn(Optional.ofNullable(chat1));
        fillChat(chat1, List.of(msg1unread, msg2unread, msg3unread));
        List<Message> res = chatService.getMessagesFromChat(chat1.getId(), 0L, 1L);
        assertEquals(res.get(0),msg3unread);
    }

    @Test
    void getMessagesFromChat_fail() {
        Long id = chat1.getId();
        ResponseStatusException res = assertThrows(ResponseStatusException.class, () -> chatService.getMessagesFromChat(id, 0L, 1L));
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void addMessageToChat_success() {
        given(chatRepository.findById(chat1.getId())).willReturn(Optional.ofNullable(chat1));
        Message res = chatService.addMessageToChat(chat1.getId(), msg1unread);
        assertEquals(msg1unread.getContent(), res.getContent());
        assertEquals(msg1unread.getFromUserId(), res.getFromUserId());
        assertEquals(msg1unread.getToUserId(), res.getToUserId());
        assertEquals(msg1unread.getMessageType(), res.getMessageType());
    }

    @Test
    void addMessageToChat_fail() {
        Long id = chat1.getId();
        ResponseStatusException res = assertThrows(ResponseStatusException.class, () -> chatService.addMessageToChat(id, msg1unread));
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void getUnreadMessages_success() {
        given(chatRepository.findById(chat1.getId())).willReturn(Optional.ofNullable(chat1));
        List<Message> res = chatService.getUnreadMessages(chat1.getId(), user1.getId());
        assertEquals(new ArrayList<>(), res);
        fillChat(chat1, List.of(msg1unread));
        res = chatService.getUnreadMessages(chat1.getId(), user1.getId());
        assertEquals(msg1unread, res.get(0));
        fillChat(chat1, List.of(msg2unread, msg3unread));
        res = chatService.getUnreadMessages(chat1.getId(), user1.getId());
        assertEquals(msg3unread, res.get(0));
        assertEquals(msg1unread, res.get(1));
    }

    @Test
    void getUnreadMessages_fail() {
        long chatId = chat1.getId();
        Long userId = user1.getId();
        ResponseStatusException res = assertThrows(ResponseStatusException.class, () -> chatService.getUnreadMessages(chatId, userId));
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void getSize_success() {
        given(chatRepository.findById(chat1.getId())).willReturn(Optional.ofNullable(chat1));
        assertEquals(2, chatService.getSize(chat1.getId()));
    }

    @Test
    void getSize_fail() {
        long chatId = chat1.getId();
        ResponseStatusException res = assertThrows(ResponseStatusException.class, () -> chatService.getSize(chatId));
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }

    @Test
    void setMessagesOfRead_success() {
        given(chatRepository.findById(chat1.getId())).willReturn(Optional.ofNullable(chat1));
        fillChat(chat1, List.of(msg1unread, msg2unread, msg3unread));
        chatService.setMessagesOfRead(chat1.getId(), user1.getId());
        assertTrue(msg1unread.isRead());
        assertFalse(msg2unread.isRead());
        assertTrue(msg3unread.isRead());
    }

    @Test
    void setMessagesOfRead_fail() {
        long chatId = chat1.getId();
        Long userId = user1.getId();
        ResponseStatusException res = assertThrows(ResponseStatusException.class, () -> chatService.setMessagesOfRead(chatId, userId));
        assertEquals(HttpStatus.NOT_FOUND, res.getStatus());
    }
}