package ch.uzh.ifi.hase.soprafs22.testHelpers;

import ch.uzh.ifi.hase.soprafs22.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs22.entity.User;

import java.util.Date;

public class UserFiller {

    protected User fillUser(Long id, String token) {
        User user = new User();
        user.setId(id);
        user.setToken(token);
        return user;
    }

    protected User fillUser(Long id, String name, String userName, String password) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setUsername(userName);
        user.setPassword(password);
        return user;
    }

    protected User fillUserToken(Long id, String token, String userName, String password) {
        User user = fillUser(id, token);
        user.setUsername(userName);
        user.setPassword(password);
        return user;
    }

    protected User fillUser(Long id, String token, String userName, String password, UserStatus status, Date birthday) {
        User user = fillUserToken(id, token, userName, password);
        user.setStatus(status);
        user.setBirthday(birthday);
        return user;
    }
}
