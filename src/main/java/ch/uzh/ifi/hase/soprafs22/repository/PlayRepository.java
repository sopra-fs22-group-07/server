package ch.uzh.ifi.hase.soprafs22.repository;


import ch.uzh.ifi.hase.soprafs22.entity.Play;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("playRepository")
public interface PlayRepository extends JpaRepository<Play, Long> {
    Play findById(long id);
}