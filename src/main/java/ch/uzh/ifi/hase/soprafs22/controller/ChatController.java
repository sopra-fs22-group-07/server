package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Match;
import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessageGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessagePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatOverViewGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.ChatService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RestController
public class ChatController {

  private final UserService userService;
  private final ChatService chatService;


  ChatController(UserService userService, ChatService chatService) {
    this.userService = userService;
    this.chatService = chatService;
  }


  @GetMapping("/users/{userId}/chats")
  @ResponseBody
  public ResponseEntity<List<ChatOverViewGetDTO>> getFirstMessageOfEveryChat(@RequestHeader(value = "authorization", required = false) String token,
                                                                             @PathVariable(value = "userId") long userId) {
    userService.checkSpecificAccess(token, userId); // 404, 409

      // get User by ID
    User user = userService.getUserById(userId);
    // get all matches from user
    List<Match> matches = userService.getMatches(user);
    // get chatID
    List<Long> chatIds = userService.getChatIds(matches);
    // get matchedUser
    List<User> usersMatched = userService.getUsersFromMatches(user, matches);
    // get the last Message from the matches/ chats
    List<Message> msg = chatService.getFirstMessages(matches);

    List<ChatOverViewGetDTO> chatOverViewGetDTOList = new ArrayList<>();

      // map messages, go through both lists and add them to chatOverViewGetDTOList
      Iterator<User> matchedUser = usersMatched.iterator();
      Iterator<Message> message = msg.iterator();
      Iterator<Long> chatId = chatIds.iterator();

      while (matchedUser.hasNext() && message.hasNext() && chatId.hasNext()) {
          ChatOverViewGetDTO chatOverViewGetDTO = new ChatOverViewGetDTO();
          chatOverViewGetDTO.setUser(DTOMapper.INSTANCE.convertEntityToUserGetDTO(matchedUser.next()));
          chatOverViewGetDTO.setMessage(message.next());
          chatOverViewGetDTO.setChatId(chatId.next());
          chatOverViewGetDTOList.add(chatOverViewGetDTO);
      }

    // I would not use the mapper here but do it myself
    return new ResponseEntity<>(chatOverViewGetDTOList, null, HttpStatus.OK);
  }


  @GetMapping("/users/{userId}/chats/{chatId}")
  @ResponseBody
  public ResponseEntity<List<ChatMessageGetDTO>> getMessagesFromChat(@RequestHeader(value = "authorization", required = false) String token,
                                                               @PathVariable(value = "userId") long userId,
                                                               @PathVariable(value = "chatId") long chatId,
                                                               @RequestParam(value = "from", required = false) long from,
                                                               @RequestParam(value = "to", required = false) long to) {
    userService.checkSpecificAccess(token, userId); // 404, 409


    List<Message> messagesFromChat = chatService.getMessagesFromChat(chatId, from, to);

      List<ChatMessageGetDTO> chatMessageGetDTOList= new ArrayList<>();

      // map the messages
      for (Message message : messagesFromChat){
          chatMessageGetDTOList.add(DTOMapper.INSTANCE.convertMessageToChatMessageGetDTO(message));
      }


      return new ResponseEntity<>(chatMessageGetDTOList, null, HttpStatus.OK);
  }


  @PostMapping("/users/{userId}/chats/{chatId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ChatMessageGetDTO createMessage(@RequestHeader(value = "authorization", required = false) String token,
                             @PathVariable(value = "userId") long userId,
                             @PathVariable(value = "chatId") long chatId,
                             @RequestBody ChatMessagePutDTO chatMessagePutDTO) {
    userService.checkSpecificAccess(token, userId); // 404, 409
      // add message to chat
    Message message = DTOMapper.INSTANCE.convertChatMessagePutDTOToEntity(chatMessagePutDTO);

    Message msg = chatService.addMessageToChat(chatId, message);
    return DTOMapper.INSTANCE.convertMessageToChatMessageGetDTO(msg);
  }

    @GetMapping("/users/{userId}/chats/{chatId}/newMsgs")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<List<ChatMessageGetDTO>> getNewMessages(@RequestHeader(value = "authorization", required = false) String token,
                                  @PathVariable(value = "userId") long userId,
                                  @PathVariable(value = "chatId") long chatId) {
        userService.checkSpecificAccess(token, userId); // 404, 409

        List<Message> unreadMessages = chatService.getUnreadMessages(chatId, userId);
        // return the black cards
        List<ChatMessageGetDTO> chatMessageGetDTOList= new ArrayList<>();

        // map messages
        for (Message message : unreadMessages){
            chatMessageGetDTOList.add(DTOMapper.INSTANCE.convertMessageToChatMessageGetDTO(message));
        }

        return new ResponseEntity<>(chatMessageGetDTOList, null, HttpStatus.OK);

    }

    @PutMapping("/users/{userId}/chats/{chatId}/read")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void readMessages(@RequestHeader(value = "authorization", required = false) String token,
                                  @PathVariable(value = "userId") long userId,
                                  @PathVariable(value = "chatId") long chatId) {
        userService.checkSpecificAccess(token, userId); // 404, 409

        // Set all to read

      chatService.setMessagesOfRead(chatId, userId);
    }

    @GetMapping("/users/{userId}/chats/{chatId}/size")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public int getSizeOfChat(@RequestHeader(value = "authorization", required = false) String token,
                                 @PathVariable(value = "userId") long userId,
                                 @PathVariable(value = "chatId") long chatId) {
        userService.checkSpecificAccess(token, userId); // 404, 409

        return chatService.getSize(chatId);
    }



}