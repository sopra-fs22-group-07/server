package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.Match;
import ch.uzh.ifi.hase.soprafs22.entity.Message;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@Transactional
public class MatchService {

  private final MatchRepository matchRepository;
  private final UserRepository userRepository;
  private final ChatRepository chatRepository;
  private final MessageRepository messageRepository;

  @Autowired
  public MatchService(@Qualifier("MatchRepository")MatchRepository matchRepository,
                      @Qualifier("userRepository") UserRepository userRepository,
                      @Qualifier("ChatRepository") ChatRepository chatRepository,
                      @Qualifier("MessageRepository") MessageRepository messageRepository) {

    this.matchRepository = matchRepository;
    this.userRepository = userRepository;
    this.chatRepository = chatRepository;
    this.messageRepository = messageRepository;
  }


  public List<Match> getMatches(User user){
      Set<Long> matchesOfUser = user.getMatches();
      List<Match> matches = new ArrayList<>();
      for(Long matchId : matchesOfUser){
          matches.add(matchRepository.getOne(matchId));
      }

      return matches;
  }

  public List<Message> getFirstMessages(List<Match> matches){

      List<Message> firstMessages = new ArrayList<>();

      for(Match match : matches){
          firstMessages.add(match.getChat().getMessages(0));
      }

      return firstMessages;

  }
}
