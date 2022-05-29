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

    // SQL
    @Query(value = """
select *  from game g
where g.game_status = 'ACTIVE'
  and g.user_id <> :userId
  and g not in (
      select g from game g
          join play p on g.id = p.game_id
      where p.user_id= :userId)
  and g.user_id in (
      select p.user_id from player p
      where not exists (
          select * from user_matches m1, user_matches m2
          where m1.matches_id = m2.matches_id and m1.users_user_id = :userId and m2.users_user_id = p.user_id))
  and g.user_id in (
      select p.user_id from player p
      where not exists (
          select * from blocked_user_relation b1, blocked_user_relation b2
          where b1.blocked_user_relations_id = b2.blocked_user_relations_id and b1.users_user_id = :userId and b2.users_user_id = p.user_id))
  and g in (
      select g from game g
          join player p on g.user_id=p.user_id
      where p.birthday >= :maxAgeDate and p.birthday <= :minAgeDate)
  and g in (
      select g from game g, player p1, player p2
      where p1.user_id = g.user_id
        and :gender in
            (select gender_preferences from user_gender_preferences ugp
            where ugp.user_user_id=p1.user_id)
        and p1.gender in
            (select gender_preferences from user_gender_preferences ugp
            where ugp.user_user_id=p2.user_id)
        and p2.user_id = :userId)
""",
            nativeQuery = true)
    Page<Game> getOtherUserWithActiveGameThatWasNotPlayedOn(Pageable pageable,
                                                            @Param("userId") long userId,
                                                            @Param("gender") String gender,
                                                            @Param("minAgeDate") Timestamp minAgeDate,
                                                            @Param("maxAgeDate") Timestamp maxAgeDate);


    @Query(value = """
select count (*)  from game g
where g.game_status = 'ACTIVE'
  and g.user_id <> :userId
  and g not in (
      select g from game g
          join play p on g.id = p.game_id
      where p.user_id= :userId)
  and g.user_id in (
      select p.user_id from player p
      where not exists (
          select * from user_matches m1, user_matches m2
          where m1.matches_id = m2.matches_id and m1.users_user_id = :userId and m2.users_user_id = p.user_id))
  and g.user_id in (
      select p.user_id from player p
      where not exists (
          select * from blocked_user_relation b1, blocked_user_relation b2
          where b1.blocked_user_relations_id = b2.blocked_user_relations_id and b1.users_user_id = :userId and b2.users_user_id = p.user_id))
  and g in (
      select g from game g
          join player p on g.user_id=p.user_id
      where p.birthday >= :maxAgeDate and p.birthday <= :minAgeDate)
  and g in (
      select g from game g, player p1, player p2
      where p1.user_id = g.user_id
        and :gender in
            (select gender_preferences from user_gender_preferences ugp
            where ugp.user_user_id=p1.user_id)
        and p1.gender in
            (select gender_preferences from user_gender_preferences ugp
            where ugp.user_user_id=p2.user_id)
        and p2.user_id = :userId)
""",
            nativeQuery = true)
    Long countOtherUserWithActiveGameThatWasNotPlayedOn(@Param("userId") long userId,
                                                        @Param("gender") String gender,
                                                        @Param("minAgeDate") Timestamp minAgeDate,
                                                        @Param("maxAgeDate") Timestamp maxAgeDate);

}

