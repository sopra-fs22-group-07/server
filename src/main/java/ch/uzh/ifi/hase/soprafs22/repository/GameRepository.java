package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.entity.Game;
import ch.uzh.ifi.hase.soprafs22.entity.Play;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Set;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {

    Game findById(long id);
    /*
    // JPQL
    //Has to check both directions becauJPQLse user only wants to see cards from people that user is interested in and that are interested in user
    @Query(value = "select game from Game game where game.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and game.user <> :player " +
            "and game not in (select g from Game g join Play p on g.id = p.gameId where p.userId = :userId)" +
            "and game in (select g from Game g, User u where u = g.user and u not in :blocked and u not in :matched)"+
            "and game in (select g from Game g join User u on g.user=u where u.birthday between :maxAgeDate and :minAgeDate)" +
            "and game in(select g from Game g, User u, User u2 where u = g.user and :gender member of u.genderPreferences and u.gender member of u2.genderPreferences and u2.id = :userId)") //We cannot pass genderPreferences as it is a set but should be a list i think
    */
    // SQL
    @Query(value = "select * \n" +
            "from game g \n" +
            "where g.game_status = 'ACTIVE'\n" +
            "and g.user_id <> :userId\n" +
            "and g not in (\n" +
            "\t select g from game\n" +
            "\t join play p on g.id = p.game_id\n" +
            "\t where p.user_id= :userId)\n" +
            " and g.user_id in (\n" +
            "\t select p.user_id\n" +
            "\t from player p\n" +
            "\t where not exists (\n" +
            "\t\t select *\n" +
            "\t\t from user_matches m\n" +
            "\t\t where p.user_id = m.users_user_id))\n" +
            "and g.user_id in (\n" +
            "\tselect p.user_id\n" +
            "\tfrom player p\n" +
            "\twhere not exists (\n" +
            "\t\tselect *\n" +
            "\t\tfrom blocked_user_relation b\n" +
            "\t\twhere p.user_id = b.users_user_id))\n" +
            "and g in (\n" +
            "\tselect g\n" +
            "\tfrom game g\n" +
            "\tjoin player p on g.user_id=p.user_id\n" +
            "where p.birthday >= :minAgeDate and p.birthday <= :maxAgeDate)" +
            "and g in\n" +
            "\t(select g from game g, player p1, player p2\n" +
            "\t where p1.user_id = g.user_id\n" +
            "\t and :gender in\n" +
            "\t (select gender_preferences\n" +
            "\t from user_gender_preferences ugp\n" +
            "\t where ugp.user_user_id=p1.user_id)\n" +
            "\t and p1.gender in \n" +
            "\t (select gender_preferences\n" +
            "\t from user_gender_preferences ugp\n" +
            "\t where ugp.user_user_id=p2.user_id)\n" +
            "\t and p2.user_id = :userId)",
            nativeQuery = true)
    Page<Game> getOtherUserWithActiveGameThatWasNotPlayedOn(Pageable pageable,

                                                            @Param("userId") long userId,
                                                            //@Param("player") User user,
                                                            @Param("gender") String gender,
                                                            @Param("minAgeDate") Date minAgeDate,
                                                            @Param("maxAgeDate") Date maxAgeDate);
                                                            //@Param("blocked") Set<User> blocked,
                                                            //@Param("matched") Set<User> matched);

    /* @Query(value = "select count (game) from Game game where game.gameStatus = ch.uzh.ifi.hase.soprafs22.constant.GameStatus.ACTIVE " +
            "and game.user <> ?2 " +
            "and game not in (select g from Game g join Play p on g.id = p.gameId where p.userId = ?1)" +
            "and game in (select g from Game g, User u where u = g.user and u not in ?6 and u not in ?7)"+
            "and game in (select g from Game g join User u on g.user=u where u.birthday between ?5 and ?4)" +
            "and game in(select g from Game g, User u, User u2 where u = g.user and ?3 member of u.genderPreferences and u.gender member of u2.genderPreferences and u2.id = ?1)")
    */
    @Query(value = "select count(*) \n" +
            "from game g \n" +
            "where g.game_status = 'ACTIVE'\n" +
            "and g.user_id <> :userId\n" +
            "and g not in (\n" +
            "\t select g from game\n" +
            "\t join play p on g.id = p.game_id\n" +
            "\t where p.user_id= :userId)\n" +
            " and g.user_id in (\n" +
            "\t select p.user_id\n" +
            "\t from player p\n" +
            "\t where not exists (\n" +
            "\t\t select *\n" +
            "\t\t from user_matches m\n" +
            "\t\t where p.user_id = m.users_user_id))\n" +
            "and g.user_id in (\n" +
            "\tselect p.user_id\n" +
            "\tfrom player p\n" +
            "\twhere not exists (\n" +
            "\t\tselect *\n" +
            "\t\tfrom blocked_user_relation b\n" +
            "\t\twhere p.user_id = b.users_user_id))\n" +
            "and g in (\n" +
            "\tselect g\n" +
            "\tfrom game g\n" +
            "\tjoin player p on g.user_id=p.user_id\n" +
            "where p.birthday >= :minAgeDate and p.birthday <= :maxAgeDate)" +
            "and g in\n" +
            "\t(select g from game g, player p1, player p2\n" +
            "\t where p1.user_id = g.user_id\n" +
            "\t and :gender in\n" +
            "\t (select gender_preferences\n" +
            "\t from user_gender_preferences ugp\n" +
            "\t where ugp.user_user_id=p1.user_id)\n" +
            "\t and p1.gender in \n" +
            "\t (select gender_preferences\n" +
            "\t from user_gender_preferences ugp\n" +
            "\t where ugp.user_user_id=p2.user_id)\n" +
            "\t and p2.user_id = :userId)",
            nativeQuery = true)
    Long countOtherUserWithActiveGameThatWasNotPlayedOn(@Param("userId") long userId,
                                                        //@Param("player") User user,
                                                        @Param("gender") String gender,
                                                        @Param("minAgeDate") Date minAgeDate,
                                                        @Param("maxAgeDate") Date maxAgeDate);
                                                        //Set<User> blocked,
                                                        //Set<User> matched);

}

