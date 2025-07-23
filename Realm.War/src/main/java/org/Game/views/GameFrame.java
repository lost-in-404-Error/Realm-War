package org.Game.views;

import org.Game.controllers.GameController;
import org.Game.models.GameState;
import org.Game.models.Kingdom;
import org.Game.models.Position;
import org.Game.models.units.Unit;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final ActionPanel actionPanel;
    private final MenuPanel menuPanel;
    private final GameController gameController;

    private Timer turnTimer;
    private Timer resourceTimer;
    private int turnSecondsLeft;

    private static final int TURN_TIME = 30;
    private static final int RESOURCE_INTERVAL = 3000;

    private Position selectedPosition;

    private boolean moveMode = false;
    private boolean attackMode = false;
    private boolean mergeMode = false;
    private boolean gamePaused = false;

    public GameFrame(GameController controller, GamePanel panel) {
        this.gameController = controller;
        this.gamePanel = panel;
        setTitle("Realm War");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        infoPanel = new InfoPanel();
        actionPanel = new ActionPanel();
        menuPanel = new MenuPanel();
        MenuPanel menuPanel = new MenuPanel();
        menuPanel.setGameControllerAndPanel(gameController, gamePanel);

        setJMenuBar(menuPanel.getMenuBar());
        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(actionPanel, BorderLayout.SOUTH);


        menuPanel.setOnStopListener(e -> stopGame());

        menuPanel.setOnResumeListener(e -> resumeGame());

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

        actionPanel.addRecruitListener(e -> JOptionPane.showMessageDialog(this, "Recruit menu opened."));
        actionPanel.addRecruitUnitListener("peasant", e -> tryRecruitUnit("peasant"));
        actionPanel.addRecruitUnitListener("spearman", e -> tryRecruitUnit("spearman"));
        actionPanel.addRecruitUnitListener("swordman", e -> tryRecruitUnit("swordman"));
        actionPanel.addRecruitUnitListener("knight", e -> tryRecruitUnit("knight"));

        actionPanel.addMergeListener(e -> {
            moveMode = false;
            attackMode = false;
            mergeMode = true;
            gameController.setSelectedUnit(null);
            gamePanel.setMoveMode(false);
            gamePanel.setAttackMode(false);
            gamePanel.setMergeMode(true);
            actionPanel.setActionsEnabled(false);
            actionPanel.setEndTurnEnabled(true);
            JOptionPane.showMessageDialog(this, "Merge mode enabled. Select first unit.");
        });

        actionPanel.addMoveListener(e -> {
            moveMode = true;
            attackMode = false;
            mergeMode = false;
            gameController.setSelectedUnit(null);
            actionPanel.setActionsEnabled(false);
            actionPanel.setEndTurnEnabled(true);
            JOptionPane.showMessageDialog(this, "Move mode enabled. Select a unit to move.");
            gamePanel.setMoveMode(true);
            gamePanel.setAttackMode(false);
            gamePanel.setMergeMode(false);
        });

        actionPanel.addAttackListener(e -> {
            attackMode = true;
            moveMode = false;
            mergeMode = false;
            gameController.setSelectedUnit(null);
            gamePanel.setAttackMode(true);
            gamePanel.setMoveMode(false);
            gamePanel.setMergeMode(false);
            actionPanel.setActionsEnabled(false);
            actionPanel.setEndTurnEnabled(true);
            JOptionPane.showMessageDialog(this, "Attack mode enabled. Select attacker.");
        });


        actionPanel.addBuildStructureListener("farm", e -> tryBuildStructure("farm"));
        actionPanel.addBuildStructureListener("barrack", e -> tryBuildStructure("barrack"));
        actionPanel.addBuildStructureListener("tower", e -> tryBuildStructure("tower"));
        actionPanel.addBuildStructureListener("market", e -> tryBuildStructure("market"));

        gamePanel.setPositionSelectListener(pos -> {
            selectedPosition = pos;
            Kingdom currentKingdom = gameController.getGameState().getCurrentKingdom();
            Unit clickedUnit = gameController.getGameState().getUnitAt(pos);

            if (moveMode) {
                handleMove(clickedUnit, pos, currentKingdom);
            } else if (attackMode) {
                handleAttack(clickedUnit, pos, currentKingdom);
            } else if (mergeMode) {
                handleMerge(clickedUnit, currentKingdom);
            }
        });
    }

    private void handleMove(Unit clickedUnit, Position pos, Kingdom currentKingdom) {
        if (gameController.getSelectedUnit() == null) {
            if (clickedUnit != null && clickedUnit.getKingdomId() == currentKingdom.getId()) {
                gameController.setSelectedUnit(clickedUnit);
                JOptionPane.showMessageDialog(this, "Unit selected. Now select destination to move.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select your own unit to move.");
            }
        } else {
            boolean success = gameController.tryMoveUnit(gameController.getSelectedUnit(), pos);
            if (success) {
                JOptionPane.showMessageDialog(this, "Unit moved successfully.");
                moveMode = false;
                gameController.setSelectedUnit(null);
                gamePanel.setMoveMode(false);
                actionPanel.setActionsEnabled(true);
                updateInfoPanel();
                gamePanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Move failed. Invalid destination.");
            }
        }
    }

    private void handleAttack(Unit clickedUnit, Position pos, Kingdom currentKingdom) {
        if (gameController.getSelectedUnit() == null) {
            if (clickedUnit != null && clickedUnit.getKingdomId() == currentKingdom.getId()) {
                gameController.setSelectedUnit(clickedUnit);
                JOptionPane.showMessageDialog(this, "Attacker selected. Now select target to attack.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select your own unit to attack.");
            }
        } else {
            boolean success = gameController.tryAttack(gameController.getSelectedUnit().getPosition(), pos);
            if (success) {
                JOptionPane.showMessageDialog(this, "Attack successful.");
                attackMode = false;
                gameController.setSelectedUnit(null);
                gamePanel.setAttackMode(false);
                actionPanel.setActionsEnabled(true);
                updateInfoPanel();
                gamePanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Attack failed.");
            }
        }
    }

    private void handleMerge(Unit clickedUnit, Kingdom currentKingdom) {
        if (gameController.getSelectedUnit() == null) {
            if (clickedUnit != null && clickedUnit.getKingdomId() == currentKingdom.getId()) {
                gameController.setSelectedUnit(clickedUnit);
                JOptionPane.showMessageDialog(this, "First unit selected. Now select the second unit to merge.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select one of your own units.");
            }
        } else {
            if (clickedUnit != null && clickedUnit.getKingdomId() == currentKingdom.getId()) {
                boolean success = gameController.tryMerge(gameController.getSelectedUnit(), clickedUnit);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Units merged successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Merge failed. The units may not be of the same type.");
                }
                mergeMode = false;
                gameController.setSelectedUnit(null);
                gamePanel.setMergeMode(false);
                actionPanel.setActionsEnabled(true);
                updateInfoPanel();
                gamePanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "The second unit must also be your own.");
            }
        }
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

    private void tryRecruitUnit(String unitType) {
        if (selectedPosition == null) {
            JOptionPane.showMessageDialog(this, "No position selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean success = gameController.tryRecruitUnit(unitType, selectedPosition);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Failed to recruit " + unitType + ".", "Error", JOptionPane.ERROR_MESSAGE);
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
            if (!gamePaused) {
                gameController.getGameState().getCurrentKingdom().updateResources();
                updateInfoPanel();
            }
        });
        resourceTimer.start();
    }


    private void endTurn() {
        turnTimer.stop();
        resourceTimer.stop();
        moveMode = false;
        attackMode = false;
        mergeMode = false;
        gameController.setSelectedUnit(null);
        gameController.nextTurn();
        updateUIForCurrentPlayer();
        turnSecondsLeft = TURN_TIME;
        turnTimer.start();
        resourceTimer.start();
        actionPanel.setActionsEnabled(true);
    }

    private void updateUIForCurrentPlayer() {
        actionPanel.setActionsEnabled(true);
        updateInfoPanel();
        gamePanel.repaint();
    }

    private void updateInfoPanel() {
        GameState gameState = gameController.getGameState();
        infoPanel.updateInfo(gameState);
    }


    public void stopGame() {
        if (turnTimer != null) turnTimer.stop();
        if (resourceTimer != null) resourceTimer.stop();
        gameController.pauseGame();
        gamePaused = true;
        moveMode = false;
        attackMode = false;
        mergeMode = false;
        gameController.setSelectedUnit(null);
        actionPanel.setActionsEnabled(false);
        JOptionPane.showMessageDialog(this, "Game paused.");
    }


    public void resumeGame() {
        if (turnTimer != null) turnTimer.start();
        if (resourceTimer != null) resourceTimer.start();
        gameController.resumeGame(); gameController.resumeGame();
        gamePaused = false;
        actionPanel.setActionsEnabled(true);
        JOptionPane.showMessageDialog(this, "Game resumed.");
    }


    public static void main(String[] args) {
        GameState gameState = new GameState(15, 10, 2);
        GameController gameController = new GameController(gameState);

        GamePanel gamePanel = new GamePanel(gameState);

        gameController.setGamePanel(gamePanel);

        gameController.startGame();

        SwingUtilities.invokeLater(() -> new GameFrame(gameController, gamePanel));
    }

}
