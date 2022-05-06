package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.MessageType;
import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatCreationPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessageGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessagePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatOverViewGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.ChatService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChatController {

  private final UserService userService;
  private final ChatService chatService;


  ChatController(ChatService chatService, UserService userService) {
    this.chatService = chatService;
    this.userService = userService;
  }


  @GetMapping("/users/{userId}/chats")
  @ResponseBody
  public ResponseEntity<ChatOverViewGetDTO> getFirstMessageOfEveryChat(@RequestHeader(value = "authorization", required = false) String token,
                                                                @PathVariable(value = "userId") long userId) {
    userService.checkSpecificAccess(token, userId); // 404, 409
    User user = new User();
    user.setId(5L);
    user.setGender(Gender.MALE);
    user.setName("David");

    User self = userService.getUserById(userId);
    Message msg = new Message();
    msg.setId(6L);
    msg.setMessageType(MessageType.PLAIN_TEXT);
    msg.setRead(false);
    msg.setFromUserId(user.getId());
    msg.setToUserId(self.getId());
    msg.setContent("Hi, how are you");

    ChatOverViewGetDTO chatOverViewGetDTO = new ChatOverViewGetDTO();
    chatOverViewGetDTO.setUser(user);
    chatOverViewGetDTO.setMessage(msg);

    // I would not use the mapper here but do it myself
    return new ResponseEntity<>(chatOverViewGetDTO, null, HttpStatus.OK);
  }


  @GetMapping("/users/{userId}/chats/{chatId}")
  @ResponseBody
  public ResponseEntity<ChatMessageGetDTO> getMessagesFromChat(@RequestHeader(value = "authorization", required = false) String token,
                                                               @PathVariable(value = "userId") long userId,
                                                               @PathVariable(value = "chatId") long chatId,
                                                               @RequestParam(value = "from", required = false) long from,
                                                               @RequestParam(value = "to", required = false) long to) {
    userService.checkSpecificAccess(token, userId); // 404, 409

    // you can use the mapper here
    ChatMessageGetDTO chatMessageGetDTO = DTOMapper.INSTANCE.convertMessageToChatMessageGetDTO(new Message());
    return new ResponseEntity<>(null, null, null);
  }


  @GetMapping("/users/{userId}/chats/DOCHUNTIRGENDEBBISABRWEISSNONIWAS")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public boolean hasUnreadMessages(@RequestHeader(value = "authorization", required = false) String token,
                                   @PathVariable(value = "userId") long userId) {
    userService.checkSpecificAccess(token, userId); // 404, 409
    return false;
  }


  @PostMapping("/users/{userId}/chats")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void createNewChat(@RequestHeader(value = "authorization", required = false) String token,
                            @PathVariable(value = "userId") long userId,
                            @RequestBody ChatCreationPostDTO chatCreationPostDTO) {
    userService.checkSpecificAccess(token, userId); // 404, 409
    long otherUserId = chatCreationPostDTO.getUserId();
    // remove if not needed:
    User otherUser = userService.getUserById(otherUserId); // 404

  }


  @PutMapping("/users/{userId}/chats/{chatId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void receiveMessage(@RequestHeader(value = "authorization", required = false) String token,
                             @PathVariable(value = "userId") long userId,
                             @PathVariable(value = "chatId") long chatId,
                             @RequestBody ChatMessagePutDTO chatMessagePutDTO) {
    userService.checkSpecificAccess(token, userId); // 404, 409
    // you SHOULD definitely use this:
    Message message = DTOMapper.INSTANCE.convertChatMessagePutDTOToEntity(chatMessagePutDTO);

  }
}