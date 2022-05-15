package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Match;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("MatchRepository")
public interface MatchRepository extends JpaRepository<Match, Long> {
  Match findByMatchId(long matchId);

  @Query("select m from Match m where (m.user1 = :user1 and m.user2 = :user2) or (m.user1 = :user2 and m.user2 = :user1) ")
  Match getMatchByUserPair(@Param("user1") User user1, @Param("user2") User user2);

  @Query("select count(m) from Match m where (m.user1 = :user1 and m.user2 = :user2) or (m.user1 = :user2 and m.user2 = :user1) ")
  int countMatchByUserPair(@Param("user1") User user1, @Param("user2") User user2);

}
