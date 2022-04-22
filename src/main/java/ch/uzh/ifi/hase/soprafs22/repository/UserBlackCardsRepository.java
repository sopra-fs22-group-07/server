package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.UserBlackCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userBlackCardsRepository")
public interface UserBlackCardsRepository extends JpaRepository<UserBlackCards, Long> {
}