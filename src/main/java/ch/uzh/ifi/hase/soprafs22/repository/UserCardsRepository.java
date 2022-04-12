package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.UserCards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("userCardsRepository")
public interface UserCardsRepository extends JpaRepository<UserCards, Long> {
  UserCards findById(long id);
}
