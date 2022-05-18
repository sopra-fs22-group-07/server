package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.WhiteCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("WhiteCardRepository")
public interface WhiteCardRepository extends JpaRepository<WhiteCard, Long> {
  WhiteCard findById(long id);
  WhiteCard findByText(String text);

  @Query("select whiteCard.id from WhiteCard whiteCard")
  List<Long> getAllIds();
}
