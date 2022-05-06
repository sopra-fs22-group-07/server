package ch.uzh.ifi.hase.soprafs22.rest.dto;


import ch.uzh.ifi.hase.soprafs22.constant.Gender;

import java.util.List;
import java.util.Set;

public class UserGetPreferencesDTO {

    private int minAge;
    private int maxAge;
    private Set<Gender> genderPreferences;


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
