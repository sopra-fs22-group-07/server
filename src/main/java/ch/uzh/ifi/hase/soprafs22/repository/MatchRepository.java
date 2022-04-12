package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("MatchRepository")
public interface MatchRepository extends JpaRepository<Match, Long> {
  Match findByMatchId(long matchId);
}
