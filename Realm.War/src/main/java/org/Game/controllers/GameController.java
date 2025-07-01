package org.Game.controllers;

import org.Game.models.GameState;

public class GameController {
    GameState gameState;

    public void startGame(){
        gameState.setRunning(true);
    }

    public void stopGame(){
        gameState.setRunning(false);
    }

    public GameState getGameState() {
        return gameState;
    }
}