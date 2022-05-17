package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("BlackCardRepository")
public interface BlackCardRepository extends JpaRepository<BlackCard, Long> {
  BlackCard findById(long id);
  BlackCard findByText(String text);


  @Query("select blackCard.id from BlackCard blackCard")
  List<Long> getAllIds();
}
