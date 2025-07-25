package org.Game.views;



import org.Game.controllers.GameController;
import org.Game.models.GameState;
import org.Game.models.Kingdom;
import org.Game.utils.DatabaseManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.Timer;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class MenuPanel extends JPanel {

    private  GamePanel gamePanel;
    private JMenuBar menuBar;
    private JMenu gameMenu;
    private JMenuItem newGameItem;
    private JMenuItem loadGameItem;
    private JMenuItem saveGameItem;
    private JMenuItem exitItem;
    private JMenuItem stopItem;
    private JMenuItem resumeItem;

    private ActionListener onStopListener;
    private ActionListener onResumeListener;
    private GameController gameController;


    public void setOnStopListener(ActionListener listener) {
        this.onStopListener = listener;
    }

    public void setOnResumeListener(ActionListener listener) {
        this.onResumeListener = listener;
    }

    public MenuPanel() {

        setBackground(new Color(30, 30, 30));

        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(117, 117, 117));
        menuBar.setOpaque(true);

        gameMenu = new JMenu("Game");
        gameMenu.setForeground(Color.WHITE);
        gameMenu.setBackground(new Color(30, 30, 30));
        gameMenu.setOpaque(true);
        stopItem = createMenuItem("Stop");
        resumeItem = createMenuItem("Resume");
        newGameItem = createMenuItem("New Game");
        loadGameItem = createMenuItem("Load Game");
        saveGameItem = createMenuItem("Save Game");
        exitItem = createMenuItem("Exit");


        gameMenu.add(stopItem);
        gameMenu.add(resumeItem);
        gameMenu.add(newGameItem);
        gameMenu.add(loadGameItem);
        gameMenu.add(saveGameItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        add(menuBar);


        stopItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Game stopped");
                if (onStopListener != null) {
                    onStopListener.actionPerformed(e);
                }
            }
        });

        resumeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Game resumed");
                if (onResumeListener != null) {
                    onResumeListener.actionPerformed(e);
                }
            }
        });

        newGameItem.addActionListener(e -> {
            if (gameController != null && gamePanel != null) {
                gameController.resetGame();
                gamePanel.setGameState(gameController.getGameState());
                gamePanel.repaint();

                JOptionPane.showMessageDialog(null, "🎮 New game started!");
            } else {
                System.err.println("❌ gameController or gamePanel is not initialized!");
            }
        });


        saveGameItem.addActionListener(e -> {
            if (gameController == null || gameController.getGameState() == null) {
                JOptionPane.showMessageDialog(null, "❌ GameController or GameState is not initialized.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Game");

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();

                gameController.getGameState().saveGameState(filePath);
                JOptionPane.showMessageDialog(null, "✅ Game saved to file: " + filePath);

                GameState currentState = gameController.getGameState();
                List<Kingdom> winners = new ArrayList<>();

                if (currentState.getWinner() != null) {
                    winners.add(currentState.getWinner());
                }

                List<Kingdom> kingdoms = currentState.getKingdoms();

                DatabaseManager dbManager = new DatabaseManager();
                try {
                    int gameId = dbManager.saveGameData(winners, kingdoms);
                    if (gameId != -1) {
                        JOptionPane.showMessageDialog(null, "✅ Game also saved to database.");
                    } else {
                        JOptionPane.showMessageDialog(null, "⚠️ Failed to save game to database.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "❌ Error saving game to database: " + ex.getMessage());
                }
            }
        });

        loadGameItem.addActionListener(e -> {
            if (gameController == null) {
                JOptionPane.showMessageDialog(null, "❌ GameController is not initialized.");
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Game");

            int userSelection = fileChooser.showOpenDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                GameState loadedState = GameState.loadGameState(filePath);
                if (loadedState != null) {
                    gameController.setGameState(loadedState);
                    gamePanel.setGameState(loadedState);
                    gamePanel.repaint();

                    JOptionPane.showMessageDialog(null, "✅ Game loaded from file: " + filePath);
                    System.out.println("Game loaded successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "❌ Failed to load game.");
                }
            }
        });
        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to exit the game?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });



        fadeInMenu();
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setForeground(Color.WHITE);
        item.setBackground(new Color(50, 50, 50));
        item.setOpaque(true);
        item.setBorderPainted(false);


        item.getModel().addChangeListener(e -> {
            if (item.getModel().isArmed() || item.getModel().isRollover()) {
                item.setBackground(new Color(70, 70, 70));
            } else {
                item.setBackground(new Color(50, 50, 50));
            }
        });

        return item;
    }


    private void fadeInMenu() {
        Timer timer = new Timer(10, new ActionListener() {
            private float opacity = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity > 1f) {
                    opacity = 1f;
                    ((Timer) e.getSource()).stop();
                }
                setOpacity(opacity);
            }
        });
        timer.start();
    }

    private void setOpacity(float opacity) {
        for (Component component : menuBar.getComponents()) {
            component.setForeground(new Color(1f, 1f, 1f, opacity));
        }
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }
    public void setGameControllerAndPanel(GameController controller, GamePanel panel) {
        this.gameController = controller;
        this.gamePanel = panel;
    }

}
