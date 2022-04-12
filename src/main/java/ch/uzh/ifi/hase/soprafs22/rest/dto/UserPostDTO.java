package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;

import java.util.Date;

public class UserPostDTO {

  private String name;
  private String username;
  private String password;
  private Date birthday;
  private Gender gender;

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

  public String getPassword() {
        return password;
    }

  public void setPassword(String password) {
        this.password = password;
    }

  public Date getBirthday () {return birthday;}

  public void setBirthday(Date birthday) {this.birthday = birthday;}

  public Gender getGender(){return this.gender; }

  public void setGender(String gender) {
      if (gender.equals("MALE")) {
          this.gender = Gender.MALE;
      }
      else if (gender.equals("FEMALE")) {
          this.gender = Gender.FEMALE;
      }
      else {
          this.gender = Gender.OTHER;
      }
  }
}
