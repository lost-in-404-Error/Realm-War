package org.Game.controllers;


import org.Game.models.Player;

public class PlayerController {
    private final Player player;

    public PlayerController(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void updatePlayerInfo(int score) {
        player.setScore(score);

    }
}

