package ch.uzh.ifi.hase.soprafs22.controller;

import ch.uzh.ifi.hase.soprafs22.entity.Match;
import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatCreationPostDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessageGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatMessagePutDTO;
import ch.uzh.ifi.hase.soprafs22.rest.dto.chat.ChatOverViewGetDTO;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs22.service.ChatService;
import ch.uzh.ifi.hase.soprafs22.service.MatchService;
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
  private final MatchService matchService;
  private final ChatService chatService;


  ChatController(MatchService matchService, UserService userService, ChatService chatService) {
    this.matchService = matchService;
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
    List<Match> matches = matchService.getMatches(user);
    // get matchedUser
    List<User> usersMatched = matchService.getUsersFromMatches(user, matches);
    // get the last Message from the matches/ chats
    List<Message> msg = chatService.getFirstMessages(matches);

    List<ChatOverViewGetDTO> chatOverViewGetDTOList = new ArrayList<>();

      // mapp the messages, go through both lists and add them to chatOverViewGetDTOList
      Iterator<User> matchedUser = usersMatched.iterator();
      Iterator<Message> message = msg.iterator();

      while (matchedUser.hasNext() && message.hasNext()) {
          ChatOverViewGetDTO chatOverViewGetDTO = new ChatOverViewGetDTO();
          chatOverViewGetDTO.setUser(DTOMapper.INSTANCE.convertEntityToUserGetDTO(matchedUser.next()));
          chatOverViewGetDTO.setMessage(message.next());
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


    List<Message> messageFromChat = chatService.getMessagesFromChat(chatId, from, to);

    // return the black cards
      List<ChatMessageGetDTO> chatMessageGetDTOList= new ArrayList<>();

      // mapp the messages
      for (Message message : messageFromChat){
          chatMessageGetDTOList.add(DTOMapper.INSTANCE.convertMessageToChatMessageGetDTO(message));
      }


      return new ResponseEntity<>(chatMessageGetDTOList, null, HttpStatus.OK);
  }

    // TODO: How to implement?
  @GetMapping("/users/{userId}/chats/DOCHUNTIRGENDEBBISABRWEISSNONIWAS")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public boolean hasUnreadMessages(@RequestHeader(value = "authorization", required = false) String token,
                                   @PathVariable(value = "userId") long userId) {
    userService.checkSpecificAccess(token, userId); // 404, 409
    return false;
  }

  // TODO: instead create chat when match is created?
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
      // add message to chat
    Message message = DTOMapper.INSTANCE.convertChatMessagePutDTOToEntity(chatMessagePutDTO);

    chatService.addMessageToChat(chatId, message);

  }
}