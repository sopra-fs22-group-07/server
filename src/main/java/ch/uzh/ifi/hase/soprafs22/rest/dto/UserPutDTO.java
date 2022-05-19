package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;

import java.util.Date;
import java.util.Set;

public class UserPutDTO {

  private String username;
  private Date birthday;
  private Gender gender;
  private int minAge;
  private int maxAge;
  private int maxRange;
  private Set<Gender> genderPreferences;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

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

  public int getMaxRange(){return maxRange;}

  public void setMaxRange(int maxRange){this.maxRange = maxRange;}

  public Set<Gender> getGenderPreferences() {
        return genderPreferences;
    }

  public void setGenderPreferences(Set<Gender> genderPreferences) {
        this.genderPreferences = genderPreferences;
    }
}
