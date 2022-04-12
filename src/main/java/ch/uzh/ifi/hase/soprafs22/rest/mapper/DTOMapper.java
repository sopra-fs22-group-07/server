package ch.uzh.ifi.hase.soprafs22.rest.mapper;

import ch.uzh.ifi.hase.soprafs22.entity.*;
import ch.uzh.ifi.hase.soprafs22.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "gender", target = "gender")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "gender", target = "gender")
  UserGetDTO convertEntityToUserGetDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "gender", target = "gender")
  @Mapping(source = "creationDate", target = "creationDate")
  @Mapping(source = "activeGame", target = "activeGame")
  @Mapping(source = "pastGames", target = "pastGames")
  @Mapping(source = "userCards", target = "userCards")
  @Mapping(source = "likedByUsers", target = "likedByUsers")
  @Mapping(source = "matches", target = "matchIds")
  UserGetDetailsDTO convertEntityToUserGetDetailsDTO(User user);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "birthday", target = "birthday")
  User convertUserPutDTOtoEntity(UserPutDTO userPutDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "text", target = "text")
  CardGetDTO convertEntityToCardGetDTO(Card card);

  @Mapping(source = "id", target = "id")
  BlackCard convertGamePostDTOToEntity(CardPostDTO cardPostDTO);

  @Mapping(source = "id", target = "gameId")
  @Mapping(source = "userId", target = "userId")
  @Mapping(source = "plays", target = "plays")
  @Mapping(source = "blackCard", target = "blackCard")
  @Mapping(source = "gameStatus", target = "gameStatus")
  @Mapping(source = "creationTime", target = "creationDate")
  GameGetDTO convertEntityToGameGetDTO(Game game);

  @Mapping(source = "gameId", target = "id")
  Game convertGameIDPostDTOToEntity(GameIDPostDTO gameIDPostDTO);

}
