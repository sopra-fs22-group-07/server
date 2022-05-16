package ch.uzh.ifi.hase.soprafs22.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    Chat chat;
    Message message1;
    Message message2;
    Message message3;

    @BeforeEach
    void setUp() {
        chat = new Chat();
        message1 = new Message();
        message2 = new Message();
        message3 = new Message();

        message1.setToUserId(1L);
        message2.setToUserId(2L);
        message3.setToUserId(1L);

        message1.setContent("one");
        message2.setContent("two");
        message3.setContent("three");

        chat.pushMessage(message1);
        chat.pushMessage(message2);
        chat.pushMessage(message3);
    }

    @Test
    void pushMessageAndSize() {
        Chat otherChat = new Chat();
        otherChat.pushMessage(message1);
        assertEquals(1, otherChat.size());
    }

    @Test
    void getMessages() {
        Object[] expected = new Object[] {message3, message2, message1};
        Object[] actual = chat.getMessages(0,5).toArray();
        assertArrayEquals(expected, actual);
        actual = chat.getMessages(0,3).toArray();
        assertArrayEquals(expected, actual);
        actual = chat.getMessages(4,5).toArray();
        assertArrayEquals(new Object[]{} , actual);
    }

    @Test
    void getFirstMessage() {
        Message actual = chat.getFirstMessage();
        assertEquals(message3, actual);
    }
}