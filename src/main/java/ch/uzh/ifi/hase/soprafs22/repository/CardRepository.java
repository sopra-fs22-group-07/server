package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("CardRepository")
public interface CardRepository extends JpaRepository<Card, Long> {
  Card findById(long id);
  Card findByText(String text);
}
