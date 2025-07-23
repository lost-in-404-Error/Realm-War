package org.Game;

import org.Game.controllers.GameController;
import org.Game.models.GameState;
import org.Game.utils.DatabaseManager;
import org.Game.views.GameFrame;
import org.Game.views.GamePanel;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        GameState gameState = new GameState(10, 10, 2);

        GameController gameController = new GameController(gameState);

        GamePanel gamePanel = new GamePanel(gameState);
        gamePanel.setController(gameController);

        gameController.setGamePanel(gamePanel);
        gameController.startGame();

        SwingUtilities.invokeLater(() -> new GameFrame(gameController, gamePanel));
    }
}
