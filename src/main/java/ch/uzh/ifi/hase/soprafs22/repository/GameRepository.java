package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findById(long id);

    @Query("select g from Game g where g.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and g.userId <> :userId " +
            "and g not in (select g from Game g join Play p on g.id = p.gameId where p.userId = :userId)")
    Page<Game> getOtherUserWithActiveGameThatWasNotPlayedOn(Pageable pageable, @Param("userId") long userId);

    @Query("select count (g) from Game g where g.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and g.userId <> :userId " +
            "and g not in (select g from Game g join Play p on g.id = p.gameId where p.userId = :userId)")
    Long countOtherUserWithActiveGameThatWasNotPlayedOn(@Param("userId") long userId);

}

