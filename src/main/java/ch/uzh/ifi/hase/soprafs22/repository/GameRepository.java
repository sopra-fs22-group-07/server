package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {
    Game findById(long id);


    //Has to check both directions because user only wants to see cards from people that user is interested in and that are interested in user
    @Query("select game from Game game where game.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and game.userId <> :userId " +
            "and game not in (select g from Game g join Play p on g.id = p.gameId where p.userId = :userId)" +
            "and game not in (select g from Game g join User u on g.userId=u.id where u.age < (select u.minAge from User u where u.id = :userId)" +
            "and  u.age > (select u.maxAge from User u where u.id = :userId) " +
            "and u.minAge > (select u.age from User u where u.id = :userId) " +
            "and u.maxAge < (select u.age from User u where u.id = :userId)) " +
            "and game in(select g from Game g, User u, User u2 where u.id = g.userId and :gender member of u.genderPreferences and u.gender member of u2.genderPreferences and u2.id = :userId)") //We cannot pass genderPreferences as it is a set but should be a list i think
    Page<Game> getOtherUserWithActiveGameThatWasNotPlayedOn(Pageable pageable,
                                                            @Param("userId") long userId,
                                                            @Param("gender") Gender gender);

    @Query("select count (g) from Game g where g.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and g.userId <> :userId " +
            "and g not in (select g from Game g join Play p on g.id = p.gameId where p.userId = :userId)" +
            "and g not in (select g from Game g join User u on g.userId=u.id where u.age < (select u.minAge from User u where u.id = :userId)" +
            "and  u.age > (select u.maxAge from User u where u.id = :userId) " +
            "and u.minAge > (select u.age from User u where u.id = :userId) " +
            "and u.maxAge < (select u.age from User u where u.id = :userId)) " +
            "and g in(select g from Game g, User u1, User u2 where u1.id =:userId and u2.gender member of u1.genderPreferences and u1.gender member of u2.genderPreferences and u2.id = g.userId)")
    Long countOtherUserWithActiveGameThatWasNotPlayedOn(@Param("userId") long userId);

}

