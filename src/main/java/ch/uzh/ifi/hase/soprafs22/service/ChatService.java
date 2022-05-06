package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Transactional
public class ChatService {

  private final MatchRepository matchRepository;
  private final UserRepository userRepository;
  private final ChatRepository chatRepository;
  private final MessageRepository messageRepository;

  @Autowired
  public ChatService(@Qualifier("MatchRepository")MatchRepository matchRepository,
                     @Qualifier("userRepository") UserRepository userRepository,
                     @Qualifier("ChatRepository") ChatRepository chatRepository,
                     @Qualifier("MessageRepository") MessageRepository messageRepository) {

    this.matchRepository = matchRepository;
    this.userRepository = userRepository;
    this.chatRepository = chatRepository;
    this.messageRepository = messageRepository;
  }
}
