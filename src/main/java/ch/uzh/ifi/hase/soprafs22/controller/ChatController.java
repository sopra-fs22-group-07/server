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

    List<ChatOverViewGetDTO> chatOverViewGetDTOList = new ArrayList<>();

    for (Match match : matches) {
      ChatOverViewGetDTO chatOverViewGetDTO = new ChatOverViewGetDTO();
      chatOverViewGetDTO.setUser(DTOMapper.INSTANCE.convertUserToMiniUserGetDTO(match.getMatchedUserFromUser(user)));
      chatOverViewGetDTO.setChatId(match.getChat().getId());
      chatOverViewGetDTO.setMessage(match.getChat().getFirstMessage());
      chatOverViewGetDTO.setMatchCreationDate(match.getCreationDate());
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