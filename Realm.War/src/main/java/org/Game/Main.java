package org.Game;



import org.Game.controllers.GameController;
import org.Game.models.GameState;
import org.Game.views.GameFrame;

import javax.swing.SwingUtilities;


public class Main {
    public static void main(String[] args) {
            GameState gameState = new GameState(10, 10, 2);
            GameController gameController = new GameController(gameState);
            gameController.startGame();

            SwingUtilities.invokeLater(() -> new GameFrame(gameController));
        }

    }
