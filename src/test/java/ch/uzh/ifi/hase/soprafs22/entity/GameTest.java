package ch.uzh.ifi.hase.soprafs22.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void deletePlaysFrom() {
        Game game = new Game();
        Play play1 = new Play();
        Play play2 = new Play();
        Play play3 = new Play();
        Play play4 = new Play();
        play1.setUserId(1);
        play2.setUserId(2);
        play3.setUserId(3);
        play4.setUserId(4);
        game.enqueuePlay(play1);
        game.enqueuePlay(play2);
        game.enqueuePlay(play3);
        game.enqueuePlay(play4);
        Object[] expected = new Object[] {play1,play2,play3,play4};
        Object[] actual = game.getPlays().toArray();
        assertArrayEquals(expected, actual);
        game.deletePlaysFrom(1L);
        expected = new Object[] {play2,play3,play4};
        actual = game.getPlays().toArray();
        assertArrayEquals(expected, actual);
        game.deletePlaysFrom(3L);
        expected = new Object[] {play2,play4};
        actual = game.getPlays().toArray();
        assertArrayEquals(expected, actual);
        game.deletePlaysFrom(4L);
        expected = new Object[] {play2};
        actual = game.getPlays().toArray();
        assertArrayEquals(expected, actual);
    }
}