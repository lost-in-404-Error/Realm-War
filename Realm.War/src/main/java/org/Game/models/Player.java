package org.Game.models;

import java.io.Serializable;

public class Player implements Serializable {

    private String name;
    private int score;

    public Player( String name, int score) {

        this.name = name;
        this.score = score;
    }


    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int value) {
        this.score += value;
    }
}
