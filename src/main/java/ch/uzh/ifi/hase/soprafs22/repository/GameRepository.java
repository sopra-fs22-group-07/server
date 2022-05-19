package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findById(long id);

    //Has to check both directions because user only wants to see cards from people that user is interested in and that are interested in user
    @Query("select game from Game game where game.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and game.user <> :user " +
            "and game not in (select g from Game g join Play p on g.id = p.gameId where p.userId = :userId)" +
            "and game in (select g from Game g join User u on g.user=u where u.birthday between :maxAgeDate and :minAgeDate)" +
            "and game in(select g from Game g, User u, User u2 where u = g.user and :gender member of u.genderPreferences and u.gender member of u2.genderPreferences and u2.id = :userId)") //We cannot pass genderPreferences as it is a set but should be a list i think
    Page<Game> getOtherUserWithActiveGameThatWasNotPlayedOn(Pageable pageable,
                                                            @Param("userId") long userId,
                                                            @Param("user") User user,
                                                            @Param("gender") Gender gender,
                                                            @Param("minAgeDate") Date minAgeDate,
                                                            @Param("maxAgeDate") Date maxAgeDate);

    @Query("select count (game) from Game game where game.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and game.user <> :user " +
            "and game not in (select g from Game g join Play p on g.id = p.gameId where p.userId = :userId)" +
            "and game in (select g from Game g join User u on g.user=u where u.birthday between :maxAgeDate and :minAgeDate)" +
            "and game in(select g from Game g, User u, User u2 where u = g.user and :gender member of u.genderPreferences and u.gender member of u2.genderPreferences and u2.id = :userId)")
    Long countOtherUserWithActiveGameThatWasNotPlayedOn(@Param("userId") long userId,
                                                        @Param("user") User user,
                                                        @Param("gender") Gender gender,
                                                        @Param("minAgeDate") Date minAgeDate,
                                                        @Param("maxAgeDate") Date maxAgeDate);

}

