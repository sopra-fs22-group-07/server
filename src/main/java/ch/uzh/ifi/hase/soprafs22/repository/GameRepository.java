package ch.uzh.ifi.hase.soprafs22.repository;

import ch.uzh.ifi.hase.soprafs22.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;


@Repository("gameRepository")
public interface GameRepository extends JpaRepository<Game, Long> {

    Game findById(long id);

    // SQL
    @Query(value = "select * \n" +
            "from game g \n" +
            "where g.game_status = 'ACTIVE'\n" +
            "and g.user_id <> :userId\n" +
            "and g.id not in (\n" +
            "\t select g.id from game g \n" +
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
            "\t select p.user_id\n" +
            "\t from player p\n" +
            "\t where not exists (\n" +
            "\t\t select *\n" +
            "\t\t from blocked_user_relation b\n" +
            "\t\t where p.user_id = b.users_user_id))\n" +
            "and g.id in (\n" +
            "\t select g.id from game g\n" +
            "\t join player p on g.user_id=p.user_id\n" +
            "\t where p.birthday >= :maxAgeDate and p.birthday <= :minAgeDate)\n" +
            "and g.id in (\n" +
            "\t select g.id from game g, player p1, player p2\n" +
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
                                                            @Param("gender") String gender,
                                                            @Param("minAgeDate") Timestamp minAgeDate,
                                                            @Param("maxAgeDate") Timestamp maxAgeDate);


    @Query(value = "select count(*) \n" +
            "from game g \n" +
            "where g.game_status = 'ACTIVE'\n" +
            "and g.user_id <> :userId\n" +
            "and g.id not in (\n" +
            "\t select g.id from game g \n" +
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
            "and g.id in (\n" +
            "\t select g.id from game g\n" +
            "\t join player p on g.user_id=p.user_id\n" +
            "\t where p.birthday >= :maxAgeDate and p.birthday <= :minAgeDate)\n" +
            "and g.id in (\n" +
            "\tselect g.id from game g, player p1, player p2\n" +
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
                                                        @Param("gender") String gender,
                                                        @Param("minAgeDate") Timestamp minAgeDate,
                                                        @Param("maxAgeDate") Timestamp maxAgeDate);

}

