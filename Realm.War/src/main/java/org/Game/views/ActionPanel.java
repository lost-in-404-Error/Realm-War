package org.Game.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;

public class ActionPanel extends JPanel {
    private final JButton endTurnButton;
    private final JButton buildButton;
    private final JButton recruitButton;
    private final JButton moveButton;
    private final JButton attackButton;
    private final JButton mergeButton;

    private final JPanel buildPanel;
    private final JPanel recruitPanel;
    private final JPanel buildPanelContainer;
    private final JPanel recruitPanelContainer;

    public ActionPanel() {
        setLayout(new BorderLayout());

        JPanel mainButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        mainButtonsPanel.setPreferredSize(new Dimension(80, 100));

        endTurnButton = createButtonWithIcon("End Turn", "/img/end.png");
        buildButton = createButtonWithIcon("Build", "/img/build.png");
        recruitButton = createButtonWithIcon("Recruit", "/img/recruit.png");
        moveButton = createButtonWithIcon("Move", "/img/move.png");
        attackButton = createButtonWithIcon("Attack", "/img/attack.png");


        mergeButton = new JButton("Merge");
        mergeButton.setPreferredSize(new Dimension(130, 50));
        mergeButton.setFocusPainted(false);
        mergeButton.setToolTipText("Merge");


        mainButtonsPanel.add(buildButton);
        mainButtonsPanel.add(recruitButton);
        mainButtonsPanel.add(moveButton);
        mainButtonsPanel.add(attackButton);
        mainButtonsPanel.add(mergeButton);
        mainButtonsPanel.add(endTurnButton);

        add(mainButtonsPanel, BorderLayout.SOUTH);

        buildPanel = createBuildPanel();
        recruitPanel = createRecruitPanel();

        buildPanelContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buildPanelContainer.setPreferredSize(new Dimension(800, 100));
        buildPanelContainer.add(buildPanel);
        buildPanelContainer.setVisible(false);

        recruitPanelContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recruitPanelContainer.setPreferredSize(new Dimension(800, 100));
        recruitPanelContainer.add(recruitPanel);
        recruitPanelContainer.setVisible(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(buildPanelContainer, BorderLayout.EAST);
        topPanel.add(recruitPanelContainer, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        buildButton.addActionListener(e -> {
            boolean visible = !buildPanelContainer.isVisible();
            buildPanelContainer.setVisible(visible);
            if (visible) recruitPanelContainer.setVisible(false);
            revalidate();
            repaint();
        });

        recruitButton.addActionListener(e -> {
            boolean visible = !recruitPanelContainer.isVisible();
            recruitPanelContainer.setVisible(visible);
            if (visible) buildPanelContainer.setVisible(false);
            revalidate();
            repaint();
        });
    }

    private JPanel createBuildPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(800, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createSideButton("farm"));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(createSideButton("barrack"));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(createSideButton("tower"));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(createSideButton("market"));

        return panel;
    }

    private JPanel createRecruitPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setPreferredSize(new Dimension(800, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createSideButton("peasant"));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(createSideButton("spearman"));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(createSideButton("swordman"));
        panel.add(Box.createHorizontalStrut(10));
        panel.add(createSideButton("knight"));

        return panel;
    }

    private JButton createButtonWithIcon(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(130, 50));
        button.setFocusPainted(false);
        button.setToolTipText(text);

        URL imgURL = getClass().getResource(iconPath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaled = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(10);
        } else {
            System.err.println("Icon not found: " + iconPath);
        }
        return button;
    }

    private JButton createSideButton(String command) {
        JButton button = new JButton(command);
        button.setPreferredSize(new Dimension(130, 40));
        button.setFocusPainted(false);
        button.setToolTipText(command);

        String iconPath = "/img/" + command + ".png";
        URL imgURL = getClass().getResource(iconPath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image scaled = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(scaled));
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(8);
        } else {
            System.err.println("Icon not found: " + iconPath);
        }

        return button;
    }



    public void addEndTurnListener(ActionListener listener) {
        endTurnButton.addActionListener(listener);
    }

    public void addBuildListener(ActionListener listener) {
        buildButton.addActionListener(listener);
    }

    public void addRecruitListener(ActionListener listener) {
        recruitButton.addActionListener(listener);
    }

    public void addMoveListener(ActionListener listener) {
        moveButton.addActionListener(listener);
    }

    public void addAttackListener(ActionListener listener) {
        attackButton.addActionListener(listener);
    }

    public void addMergeListener(ActionListener listener) {
        mergeButton.addActionListener(listener);
    }

    public void addBuildStructureListener(String structureType, ActionListener listener) {
        for (Component comp : buildPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equalsIgnoreCase(structureType)) {
                    btn.addActionListener(listener);
                    break;
                }
            }
        }
    }

    public void addRecruitUnitListener(String unitType, ActionListener listener) {
        for (Component comp : recruitPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (btn.getText().equalsIgnoreCase(unitType)) {
                    btn.addActionListener(listener);
                    break;
                }
            }
        }
    }

    public void setActionsEnabled(boolean enabled) {
        buildButton.setEnabled(enabled);
        recruitButton.setEnabled(enabled);
        moveButton.setEnabled(enabled);
        attackButton.setEnabled(enabled);
        mergeButton.setEnabled(enabled);
        endTurnButton.setEnabled(enabled);
    }

    public void setEndTurnEnabled(boolean b) {
        endTurnButton.setEnabled(b);
    }


}
