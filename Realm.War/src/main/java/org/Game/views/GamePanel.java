package org.Game.views;

import org.Game.models.GameState;
import org.Game.models.Kingdom;
import org.Game.models.Position;
import org.Game.models.blocks.*;
import org.Game.models.structures.*;
import org.Game.models.units.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class GamePanel extends JPanel {
    private static final int BLOCK_SIZE = 50;

    private GameState gameState;
    private Unit selectedUnit;
    private boolean moveMode = false;

    private Consumer<Position> positionSelectListener;

    // Structure Images
    private Image farmImage;
    private Image barrackImage;
    private Image towerImage;
    private Image marketImage;

    // Unit Images
    private Image peasantImage;
    private Image spearmanImage;
    private Image swordmanImage;
    private Image knightImage;

    public GamePanel(GameState gameState) {
        this.gameState = gameState;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.DARK_GRAY);

        loadStructureImages();
        loadUnitImages();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / BLOCK_SIZE;
                int y = e.getY() / BLOCK_SIZE;
                Position clicked = new Position(x, y);

                if (positionSelectListener != null) {
                    positionSelectListener.accept(clicked);
                } else {
                    handleClick(x, y);
                }
            }
        });
    }

    private void loadStructureImages() {
        try {
            farmImage = loadImage("/img/farm.png");
            barrackImage = loadImage("/img/barrack.png");
            towerImage = loadImage("/img/tower.png");
            marketImage = loadImage("/img/market.png");
        } catch (Exception e) {
            System.err.println("Error loading structure images:");
            e.printStackTrace();
        }
    }

    private void loadUnitImages() {
        try {
            peasantImage = loadImage("/img/peasant.png");
            spearmanImage = loadImage("/img/spearman.png");
            swordmanImage = loadImage("/img/swordman.png");
            knightImage = loadImage("/img/knight.png");
        } catch (Exception e) {
            System.err.println("Error loading unit images:");
            e.printStackTrace();
        }
    }

    private Image loadImage(String path) {
        return new ImageIcon(getClass().getResource(path)).getImage();
    }

    public void setPositionSelectListener(Consumer<Position> listener) {
        this.positionSelectListener = listener;
    }

    public void setMoveMode(boolean moveMode) {
        this.moveMode = moveMode;
        if (!moveMode) selectedUnit = null;
        repaint();
    }

    private void handleClick(int x, int y) {
        if (x < 0 || x >= gameState.getGameMap().length || y < 0 || y >= gameState.getGameMap()[0].length) return;

        if (moveMode) {
            if (selectedUnit == null) {
                selectedUnit = findUnitAt(x, y);
            } else if (canMoveTo(selectedUnit, x, y)) {
                selectedUnit.setPosition(new Position(x, y));
                Block targetBlock = gameState.getGameMap()[x][y];
                gameState.getKingdomById(selectedUnit.getKingdomId()).absorbBlock(targetBlock);
                setMoveMode(false);
            }
        } else {
            System.out.println("Clicked on: " + x + "," + y);
        }

        repaint();
    }

    private Unit findUnitAt(int x, int y) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            for (Unit unit : kingdom.getUnits()) {
                if (unit.getPosition().equals(new Position(x, y))) return unit;
            }
        }
        return null;
    }

    private boolean canMoveTo(Unit unit, int x, int y) {
        int dx = Math.abs(unit.getPosition().getX() - x);
        int dy = Math.abs(unit.getPosition().getY() - y);
        int distance = dx + dy;

        if (x < 0 || y < 0 || x >= gameState.getGameMap().length || y >= gameState.getGameMap()[0].length)
            return false;

        Block block = gameState.getGameMap()[x][y];
        return !(block instanceof VoidBlock) && distance <= unit.getMovementRange();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Block[][] map = gameState.getGameMap();

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[0].length; y++) {
                drawBlock(g, map[x][y], x * BLOCK_SIZE, y * BLOCK_SIZE);
            }
        }

        for (Kingdom kingdom : gameState.getKingdoms()) {
            for (Structure structure : kingdom.getStructures()) {
                drawStructure(g, structure);
            }
        }

        for (Kingdom kingdom : gameState.getKingdoms()) {
            for (Unit unit : kingdom.getUnits()) {
                drawUnit(g, unit);
            }
        }

        if (moveMode && selectedUnit != null) {
            highlightMovementRange(g, selectedUnit);
        }
    }

    private void drawBlock(Graphics g, Block block, int x, int y) {
        if (block instanceof VoidBlock) g.setColor(Color.BLACK);
        else if (block instanceof ForestBlock) g.setColor(new Color(34, 139, 34));
        else if (block instanceof EmptyBlock) {
            if (block.isAbsorbed()) {
                g.setColor(switch (block.getOwnerID()) {
                    case 1 -> new Color(150, 13, 72);
                    case 2 -> new Color(182, 25, 25);
                    default -> Color.LIGHT_GRAY;
                });
            } else {
                g.setColor(new Color(144, 238, 144));
            }
        } else {
            g.setColor(Color.GRAY);
        }

        g.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
    }

    private void drawStructure(Graphics g, Structure structure) {
        Position pos = structure.getPosition();
        int x = pos.getX() * BLOCK_SIZE;
        int y = pos.getY() * BLOCK_SIZE;

        Image img = switch (structure) {
            case Farm ignored -> farmImage;
            case Barrack ignored -> barrackImage;
            case Tower ignored -> towerImage;
            case Market ignored -> marketImage;
            default -> null;
        };

        if (img != null) {
            g.drawImage(img, x + 5, y + 5, BLOCK_SIZE - 10, BLOCK_SIZE - 10, this);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x + 10, y + 10, 30, 30);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("" + structure.getLevel(), x + BLOCK_SIZE / 2 - 8, y + BLOCK_SIZE / 2 + 5);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("P" + structure.getKingdomID(), x + 12, y + BLOCK_SIZE - 12);
    }

    private void drawUnit(Graphics g, Unit unit) {
        Position pos = unit.getPosition();
        int x = pos.getX() * BLOCK_SIZE;
        int y = pos.getY() * BLOCK_SIZE;

        Image img = switch (unit) {
            case Peasant ignored -> peasantImage;
            case Spearman ignored -> spearmanImage;
            case Swordman ignored -> swordmanImage;
            case Knight ignored -> knightImage;
            default -> null;
        };

        if (img != null) {
            g.drawImage(img, x + 5, y + 5, BLOCK_SIZE - 10, BLOCK_SIZE - 10, this);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x + 10, y + 10, 30, 30);
        }

        g.setColor(Color.RED);
        int hpWidth = (int) ((BLOCK_SIZE - 4) * ((double) unit.getHitPoints() / unit.getMaxHitPoints()));
        g.fillRect(x + 2, y + BLOCK_SIZE - 8, hpWidth, 4);
    }

    private void highlightMovementRange(Graphics g, Unit unit) {
        int cx = unit.getPosition().getX();
        int cy = unit.getPosition().getY();
        int range = unit.getMovementRange();

        g.setColor(new Color(0, 255, 255, 80));

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int nx = cx + dx;
                int ny = cy + dy;
                if (canMoveTo(unit, nx, ny)) {
                    g.fillRect(nx * BLOCK_SIZE, ny * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                }
            }
        }

        g.setColor(Color.CYAN);
        g.drawRect(cx * BLOCK_SIZE, cy * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        repaint();
    }
}
