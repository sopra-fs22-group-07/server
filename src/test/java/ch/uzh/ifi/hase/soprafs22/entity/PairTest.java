package ch.uzh.ifi.hase.soprafs22.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void testEquals() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        Pair<User, User> pair12 = new Pair<>(user1, user2);
        Pair<User, User> pair21 = new Pair<>(user2, user1);
        Pair<User, User> pair13 = new Pair<>(user1, user3);
        Pair<User, User> pair23 = new Pair<>(user2, user3);
        assertEquals(pair12, pair12);
        assertEquals(pair12, pair21);
        assertNotEquals(pair12, pair13);
        assertNotEquals(pair13, pair23);
        assertNotEquals(null, pair12);
        assertNotEquals(pair21, new User());
    }
}