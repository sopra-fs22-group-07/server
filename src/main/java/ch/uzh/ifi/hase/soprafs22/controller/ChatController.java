package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.Match;
import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.CardGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatCreationPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessageGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessagePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatOverViewGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.MatchService;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class ChatController {

  private final UserService userService;
  private final MatchService matchService;


  ChatController(MatchService chatService, UserService userService) {
    this.matchService = chatService;
    this.userService = userService;
  }


  @GetMapping("/users/{userId}/chats")
  @ResponseBody
  public ResponseEntity<List<ChatOverViewGetDTO>> getFirstMessageOfEveryChat(@RequestHeader(value = "authorization", required = false) String token,
                                                                             @PathVariable(value = "userId") long userId) {
    userService.checkSpecificAccess(token, userId); // 404, 409

    User user = userService.getUserById(userId);

    List<Match> matches = matchService.getMatches(user);

    List<Message> msg = matchService.getFirstMessages(matches);

    List<ChatOverViewGetDTO> chatOverViewGetDTOList = new ArrayList<>();

    //TODO: map to each chatOverViewGetDTOList one match/ userID and one message

    ChatOverViewGetDTO chatOverViewGetDTO = new ChatOverViewGetDTO();

    // this should be done for all users and messages, or matches and messages?
    chatOverViewGetDTO.setUser(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    chatOverViewGetDTO.setMessage(msg.get(0));

    chatOverViewGetDTOList.add(chatOverViewGetDTO);

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


    List<Message> messageFromChat = matchService.getMessagesFromChat(chatId);
      // return the black cards
      List<ChatMessageGetDTO> chatMessageGetDTOList= new ArrayList<>();

      // mapp the messages
      for (Message message : messageFromChat){
          chatMessageGetDTOList.add(DTOMapper.INSTANCE.convertMessageToChatMessageGetDTO(message));
      }


      return new ResponseEntity<>(chatMessageGetDTOList, null, HttpStatus.OK);
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