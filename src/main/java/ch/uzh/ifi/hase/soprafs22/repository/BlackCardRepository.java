package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("BlackCardRepository")
public interface BlackCardRepository extends JpaRepository<BlackCard, Long> {
  BlackCard findById(long id);
  BlackCard findByText(String text);
}
