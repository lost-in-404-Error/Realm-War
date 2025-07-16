package org.Game.views;

import org.Game.controllers.GameController;
import org.Game.models.GameState;
import org.Game.models.Kingdom;
import org.Game.models.Position;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final ActionPanel actionPanel;

    private final GameController gameController;

    private Timer turnTimer;
    private Timer resourceTimer;
    private int turnSecondsLeft;

    private static final int TURN_TIME = 30;
    private static final int RESOURCE_INTERVAL = 3000;

    private Position selectedPosition;

    public GameFrame(GameController controller) {
        this.gameController = controller;

        setTitle("Realm War");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        gamePanel = new GamePanel(controller.getGameState());
        infoPanel = new InfoPanel();
        actionPanel = new ActionPanel();

        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        setupListeners();
        startTurnTimer();
        startResourceTimer();

        updateUIForCurrentPlayer();
    }

    private void setupListeners() {
        actionPanel.addEndTurnListener(e -> endTurn());

        actionPanel.addRecruitListener(e -> {
            JOptionPane.showMessageDialog(this, "Recruit menu opened.");
        });

        actionPanel.addMoveListener(e -> {
            gamePanel.setMoveMode(true);
        });

        actionPanel.addAttackListener(e -> {
            JOptionPane.showMessageDialog(this, "Attack mode enabled.");
        });

        actionPanel.addBuildStructureListener("farm", e -> {
            tryBuildStructure("farm");
        });

        actionPanel.addBuildStructureListener("barrack", e -> tryBuildStructure("barrack"));
        actionPanel.addBuildStructureListener("tower", e -> tryBuildStructure("tower"));
        actionPanel.addBuildStructureListener("market", e -> tryBuildStructure("market"));

        actionPanel.setActionsEnabled(true);


        gamePanel.setPositionSelectListener(pos -> {
            selectedPosition = pos;
            System.out.println("Selected position: " + pos.getX() + "," + pos.getY());
        });
    }

    private void tryBuildStructure(String type) {
        if (selectedPosition == null) {
            JOptionPane.showMessageDialog(this, "No position selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = gameController.tryBuildStructure(type, selectedPosition);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to build " + type + ".", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            gamePanel.repaint();
            updateInfoPanel();
        }
    }

    private void startTurnTimer() {
        turnSecondsLeft = TURN_TIME;
        turnTimer = new Timer(1000, e -> {
            turnSecondsLeft--;
            updateInfoPanel();

            if (turnSecondsLeft <= 0) {
                endTurn();
            }
        });
        turnTimer.start();
    }

    private void startResourceTimer() {
        resourceTimer = new Timer(RESOURCE_INTERVAL, e -> {
            gameController.getGameState().getCurrentKingdom().updateResources();
            updateInfoPanel();
        });
        resourceTimer.start();
    }

    private void endTurn() {
        turnTimer.stop();
        resourceTimer.stop();

        gameController.nextTurn();

        updateUIForCurrentPlayer();

        turnSecondsLeft = TURN_TIME;
        turnTimer.start();
        resourceTimer.start();
    }

    private void updateUIForCurrentPlayer() {
        Kingdom currentKingdom = gameController.getGameState().getCurrentKingdom();
        int playerId = currentKingdom.getId();

        actionPanel.setActionsEnabled(true);

        updateInfoPanel();
        gamePanel.repaint();
    }

    private void updateInfoPanel() {
        GameState gameState = gameController.getGameState();
        infoPanel.updateInfo(gameState);
    }

    public static void main(String[] args) {
        GameState gameState = new GameState(15, 10, 2);
        GameController gameController = new GameController(gameState);
        gameController.startGame();

        SwingUtilities.invokeLater(() -> new GameFrame(gameController));
    }
}
