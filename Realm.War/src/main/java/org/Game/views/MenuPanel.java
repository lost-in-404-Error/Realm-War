package org.Game.views;



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import javax.swing.Timer;
import java.awt.Component;

public class MenuPanel extends JPanel {
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


        newGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("New Game selected");
            }
        });

        loadGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Load Game selected");
            }
        });

        saveGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Save Game selected");
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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
}
