package org.Game.controllers;

import org.Game.models.GameState;



public class GameController {
    private final GameState gameState;
    private StructureController structureController;

    public GameController(GameState gameState) {
        this.gameState = gameState;

    }

    public void startGame() {
        gameState.startGame();
    }

    public void stopGame() {
        gameState.stopGame();
    }

    public void nextTurn() {
        gameState.endTurn();

    }

    public GameState getGameState() {
        return gameState;
    }




}
