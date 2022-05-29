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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
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
    matches.sort((o1, o2) -> (int)(o1.getCreationDate().getTime() - o2.getCreationDate().getTime()));
    // get chatID
    List<Long> chatIds = userService.getChatIds(matches);
    // get matchedUser
    List<User> usersMatched = new ArrayList<>();
    for(Match match : matches) {
      usersMatched.add(userService.getUsersFromMatches(user, match));
    }

    // get the last Message from the matches/ chats
    List<Message> msg = chatService.getFirstMessages(matches);

    List<ChatOverViewGetDTO> chatOverViewGetDTOList = new ArrayList<>();

      // map messages, go through both lists and add them to chatOverViewGetDTOList
      Iterator<User> matchedUser = usersMatched.iterator();
      Iterator<Message> message = msg.iterator();
      Iterator<Long> chatId = chatIds.iterator();
      Iterator<Match> match = matches.iterator();

      while (matchedUser.hasNext() && message.hasNext() && chatId.hasNext() && match.hasNext()) {
          ChatOverViewGetDTO chatOverViewGetDTO = new ChatOverViewGetDTO();
          chatOverViewGetDTO.setUser(DTOMapper.INSTANCE.convertUserToMiniUserGetDTO(matchedUser.next()));
          chatOverViewGetDTO.setMessage(message.next());
          chatOverViewGetDTO.setChatId(chatId.next());
          chatOverViewGetDTO.setMatchCreationDate(match.next().getCreationDate());
          chatOverViewGetDTOList.add(chatOverViewGetDTO);
      }

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
        List<ChatMessageGetDTO> chatMessageGetDTOList= new ArrayList<>();

        // map messages
        for (Message message : unreadMessages){
            chatMessageGetDTOList.add(DTOMapper.INSTANCE.convertMessageToChatMessageGetDTO(message));
        }
      MultiValueMap<String, String> headers = new HttpHeaders();

      Long u = chatService.getUserIdFromOtherUser(chatId, userId);
        if (u != null){
          User otherUser = userService.getUserById(u);
          headers.set("status", otherUser.getStatus().toString());
        }
        else {
          headers.set("status", "INACTIVE");
        }

      return new ResponseEntity<>(chatMessageGetDTOList, headers, HttpStatus.OK);

    }

    @PutMapping("/users/{userId}/chats/{chatId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
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