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
    public void calculateScore(Kingdom kingdom) {
        int score = 0;


        score += kingdom.getGold() * 1;
        score += kingdom.getFood() * 1;


        score += kingdom.getStructures().size() * 10;


        score += kingdom.getUnits().size() * 8;


        score += kingdom.getAbsorbedBlocks().size() * 5;

        this.score = score;
    }

}
