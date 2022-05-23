package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.MessageType;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessagePutDTO;
import ch.uzh.ifi.hase.soprafs22.service.ChatService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import ch.uzh.ifi.hase.soprafs22.testHelpers.ChatFiller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest extends ChatFiller {

    User user1;
    User user2;
    List<Match> matches;
    List<Long> chatIds;
    List<User> userMatched;
    List<Message> messages;

    Message msg1unread;
    Message msg2unread;
    Message msg3unread;
    Chat chat1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        user1 = fillUser(1L, "1one");
        user2 = fillUser(2L, "2two");
        User user3 = fillUser(3L, "3three");
        Pair<User, User> userPair12 = new Pair<>(user1, user2);
        Pair<User, User> userPair13 = new Pair<>(user1, user3);
        Message message1 = fillMessage(1L, 2L, "first");
        Message message2 = fillMessage(2L, 1L, "second");
        Message message3 = fillMessage(3L, 1L, "third");
        Message message4 = fillMessage(1L, 3L, "fourth");
        msg1unread = fillMessage(user2.getId(), user1.getId(), "Hi", false);
        msg2unread = fillMessage(user1.getId(), user2.getId(), "Hey", false);
        msg3unread = fillMessage(user2.getId(), user1.getId(), "Bye", false);
        chat1 = fillChat(40, List.of(message1, message2));
        Chat chat2 = fillChat(41, List.of(message3, message4));
        chatIds = List.of(chat1.getId(), chat2.getId());
        Match match1 = fillAndAddMatch(30L, userPair12, chat1);
        Match match2 = fillAndAddMatch(31L, userPair13, chat2);
        matches = new ArrayList<>();
        matches.add(match1);
        matches.add(match2);
        userMatched = List.of(user2, user3);
        messages = List.of(chat1.getFirstMessage(), chat2.getFirstMessage());
    }

    @BeforeEach
    void setUpForAuthorization() {
        doNothing().when(userService).checkSpecificAccess(isA(String.class), isA(Long.class));
    }

    @Test
    void getFirstMessageOfEveryChat_exactlyOneElement_success() throws Exception {
        given(userService.getUserById(user1.getId())).willReturn(user1);
        given(userService.getMatches(user1)).willReturn(matches.subList(0,1));
        given(userService.getChatIds(Mockito.anyList())).willReturn(chatIds.subList(0,1));
        given(userService.getUsersFromMatches(eq(user1), Mockito.anyList())).willReturn(userMatched.subList(0,1));
        given(chatService.getFirstMessages(Mockito.anyList())).willReturn(messages.subList(0,1));

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id", is(2)))
                .andExpect(jsonPath("$[0].message.content", is(messages.get(0).getContent())));
    }

    @Test
    void getFirstMessageOfEveryChat_moreThanOneElement_success() throws Exception {
        given(userService.getUserById(user1.getId())).willReturn(user1);
        given(userService.getMatches(user1)).willReturn(matches);
        given(userService.getChatIds(matches)).willReturn(chatIds);
        given(userService.getUsersFromMatches(user1, matches)).willReturn(userMatched);
        given(chatService.getFirstMessages(matches)).willReturn(messages);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id", is(2)))
                .andExpect(jsonPath("$[0].message.content", is(messages.get(0).getContent())))
                .andExpect(jsonPath("$[1].user.id", is(3)))
                .andExpect(jsonPath("$[1].message.content", is(messages.get(1).getContent())));
    }

    @Test
    void getFirstMessageOfEveryChat_NoElementToReturn() throws Exception {
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats", user1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk());
        // .andExpect(jsonPath("$", equalTo(new JSONArray())))
    }

    @Test
    void getMessagesFromChat_oneElementToReturn() throws Exception {
        int from = 0;
        int to = 1;

        given(chatService.getMessagesFromChat(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(chat1.getMessages(from,to));
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken())
                .param("from", ("" + from))
                .param("to", ("" + to));
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", is("second")))
                .andExpect(jsonPath("$[0].from", is(user2.getId().intValue())))
                .andExpect(jsonPath("$[0].to", is(user1.getId().intValue())));
    }

    @Test
    void getMessagesFromChat_severalElementsToReturn() throws Exception {
        int from = 0;
        int to = 100;

        given(chatService.getMessagesFromChat(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(chat1.getMessages(from,to));
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken())
                .param("from", ("" + from))
                .param("to", ("" + to));
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", is("second")))
                .andExpect(jsonPath("$[0].from", is(user2.getId().intValue())))
                .andExpect(jsonPath("$[0].to", is(user1.getId().intValue())))
                .andExpect(jsonPath("$[1].content", is("first")))
                .andExpect(jsonPath("$[1].from", is(user1.getId().intValue())))
                .andExpect(jsonPath("$[1].to", is(user2.getId().intValue())));
    }

    @Test
    void getMessagesFromChat_NoElementToReturn() throws Exception {
        given(chatService.getMessagesFromChat(Mockito.any(), Mockito.any(), Mockito.any())).willReturn(new ArrayList<>());
        // when
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken())
                .param("from", user1.getId().toString())
                .param("to", user2.getId().toString());
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk());
        // .andExpect(jsonPath("$", equalTo(new JSONArray())))
    }

    @Test
    void createMessage_success() throws Exception {
        ChatMessagePutDTO chatMessagePutDTO = fillDTOFromMessage(messages.get(0));
        given(chatService.addMessageToChat(eq(chat1.getId()), Mockito.any())).willReturn(messages.get(0));
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/chats/{chatId}", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatMessagePutDTO))
                .header("authorization", user1.getToken());
        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.content", is(messages.get(0).getContent())))
                .andExpect(jsonPath("$.from", is(messages.get(0).getFromUserId().intValue())))
                .andExpect(jsonPath("$.to", is(messages.get(0).getToUserId().intValue())));
    }

    @Test
    void createMessage_chatNotFound() throws Exception {
        ChatMessagePutDTO chatMessagePutDTO = fillDTOFromMessage(messages.get(0));
        given(chatService.addMessageToChat(eq(chat1.getId()), Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not exists"));
        MockHttpServletRequestBuilder postRequest = post("/users/{userId}/chats/{chatId}", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatMessagePutDTO))
                .header("authorization", user1.getToken());
        // then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    void getNewMessages_oneElement() throws Exception {
        readExistingChat();
        chat1.pushMessage(fillMessage(user2.getId(), user1.getId(), "Hi", false));
        given(chatService.getUnreadMessages(chat1.getId(), user1.getId())).willReturn(chat1.getMessages(0,1));
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}/newMsgs", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", is("Hi")));
    }

    @Test
    void getNewMessages_severalElements() throws Exception {
        readExistingChat();
        fillChat(chat1, List.of(msg1unread, msg2unread, msg3unread));
        given(chatService.getUnreadMessages(chat1.getId(), user1.getId())).willReturn(List.of(msg1unread, msg3unread));
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}/newMsgs", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content", is("Hi")))
                .andExpect(jsonPath("$[1].content", is("Bye")));
    }

    @Test
    void getNewMessages_chatNotFound() throws Exception {
        given(chatService.getUnreadMessages(chat1.getId(), user1.getId())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}/newMsgs", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    void readMessages_success() throws Exception {
        readExistingChat();
        fillChat(chat1, List.of(msg1unread, msg2unread, msg3unread));
        doNothing().when(chatService).setMessagesOfRead(chat1.getId(), user1.getId());
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/chats/{chatId}/read", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        mockMvc.perform(putRequest).andExpect(status().isNoContent());
    }

    @Test
    void readMessages_chatNotFound() throws Exception {
        readExistingChat();
        fillChat(chat1, List.of(msg1unread, msg2unread, msg3unread));
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(chatService).setMessagesOfRead(chat1.getId(), user1.getId());
        MockHttpServletRequestBuilder putRequest = put("/users/{userId}/chats/{chatId}/read", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        mockMvc.perform(putRequest).andExpect(status().isNotFound());
    }

    @Test
    void getSizeOfChat_success() throws Exception {
        given(chatService.getSize(chat1.getId())).willReturn(chat1.size());
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}/size", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", is(chat1.size())));
    }

    @Test
    void getSizeOfChat_chatNotFound() throws Exception {
        given(chatService.getSize(chat1.getId())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        MockHttpServletRequestBuilder getRequest = get("/users/{userId}/chats/{chatId}/size", user1.getId(), chat1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("authorization", user1.getToken());
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    private void readExistingChat(){
        messages.get(0).setRead(true);
        messages.get(1).setRead(true);
    }

    private ChatMessagePutDTO fillDTOFromMessage(Message message) {
        ChatMessagePutDTO chatMessagePutDTO = new ChatMessagePutDTO();
        if (message.getFromUserId() != null) {chatMessagePutDTO.setFromUserId(message.getFromUserId());}
        if(message.getToUserId() != null) {chatMessagePutDTO.setToUserId(message.getToUserId());}
        if(message.getContent() != null) {chatMessagePutDTO.setContent(message.getContent());}
        if (message.getMessageType() == null) {
            chatMessagePutDTO.setMessageType(MessageType.PLAIN_TEXT.name());
        } else {
            chatMessagePutDTO.setMessageType(message.getMessageType().name());
        }
        return chatMessagePutDTO;
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}