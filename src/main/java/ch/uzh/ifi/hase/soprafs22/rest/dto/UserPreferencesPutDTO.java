package ch.uzh.ifi.hase.soprafs22.rest.dto;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;

import java.util.List;

public class UserPreferencesPutDTO {

    private Long id;
    private int minAge;
    private int maxAge;
    private List<Gender> genderPreferences;


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

    public List<Gender> getGenderPreferences() {
        return genderPreferences;
    }

    public void setGenderPreferences(List<Gender> genderPreferences) {
        this.genderPreferences = genderPreferences;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
