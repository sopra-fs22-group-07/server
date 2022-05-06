package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;

import java.util.Date;
import java.util.Set;

public class UserGetDTO {

  private Long id;
  private String name;
  private String username;
  private UserStatus status;
  private Date birthday;
  private Gender gender;
  private Date creationDate;
  private int minAge;
  private int maxAge;
  private Set<Gender> genderPreferences;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }


  public Gender getGender(){
      return this.gender;
  }

  public void setGender(Gender gender){this.gender = gender; }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public Set<Gender> getGenderPreferences() {
        return genderPreferences;
    }

    public void setGenderPreferences(Set<Gender> genderPreferences) {
        this.genderPreferences = genderPreferences;
    }
}
