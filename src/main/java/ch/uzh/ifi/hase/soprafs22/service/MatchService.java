package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.repository.ChatRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MatchRepository;
import ch.uzh.ifi.hase.soprafs22.repository.MessageRepository;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs22.rest.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@Transactional
public class MatchService {

  private final MatchRepository matchRepository;

  @Autowired
  public MatchService(@Qualifier("MatchRepository")MatchRepository matchRepository){

    this.matchRepository = matchRepository;
  }

    /**
     * get matches of a user
     * @param user: user from which the matches are taken
     * @return Lsit of Matches
     */
  public List<Match> getMatches(User user){
      Set<Long> matchesOfUser = user.getMatches();
      List<Match> matches = new ArrayList<>();
      for(Long matchId : matchesOfUser){
          matches.add(matchRepository.getOne(matchId));
      }

      return matches;
  }

    /**
     * Get all users which match with the known user
     * @param user: known user
     * @param matches: all matches from suer
     * @return list of users which mach with known user
     */
    public List<User> getUsersFromMatches(User user, List<Match> matches) {
        List<User> matchedUsers = new ArrayList<>();

        // map the users to the matches
        for (Match match : matches){
            Pair<User,User> users = match.getUserPair();
            // add other user (by comparing it with the user, which is known)
            if(user.equals(users.getObj1())){
                matchedUsers.add(users.getObj2());
            }else{
                matchedUsers.add(users.getObj1());
            }
        }

        return matchedUsers;
    }
}
