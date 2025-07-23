package org.Game.models;

import org.Game.models.blocks.*;
import org.Game.models.structures.Structure;
import org.Game.models.structures.TownHall;
import org.Game.models.units.Unit;


import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class GameState implements Serializable {
    private int currentPlayerTurn;
    private List<Kingdom> kingdoms;
    private Block[][] gameMap;
    private int turnNumber;
    private boolean running;
    private List<Structure> structures;

    private transient ScheduledExecutorService scheduler;
    private transient ScheduledFuture<?> resourceTask;
    private transient ScheduledFuture<?> turnTask;
    private JPanel gamePanel;
    private static final int RESOURCE_INTERVAL = 3;  // seconds
    private static final int TURN_DURATION = 30;     // seconds
    private volatile boolean paused = false;

    public GameState(int mapWidth, int mapHeight, int playerCount) {
        this.kingdoms = new ArrayList<>();
        this.gameMap = new Block[mapWidth][mapHeight];
        this.currentPlayerTurn = 0;
        this.turnNumber = 1;
        this.running = false;
        this.structures = new ArrayList<>();
        initializeMap(mapWidth, mapHeight);
        initializeKingdoms(playerCount);
    }

    private void initializeMap(int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    gameMap[x][y] = new VoidBlock(new Position(x, y));
                } else if (Math.random() < 0.2) {
                    gameMap[x][y] = new ForestBlock(new Position(x, y));
                } else {
                    gameMap[x][y] = new EmptyBlock(new Position(x, y));
                }
            }
        }
    }

    private void initializeKingdoms(int playerCount) {
        int[][] startingPositions = {
                {1, 1},
                {gameMap.length - 2, gameMap[0].length - 2},
                {1, gameMap[0].length - 2},
                {gameMap.length - 2, 1}
        };

        for (int i = 0; i < playerCount; i++) {
            Position townHallPos = new Position(startingPositions[i][0], startingPositions[i][1]);
            Block baseBlock = gameMap[townHallPos.getX()][townHallPos.getY()];
            TownHall townHall = new TownHall(townHallPos, baseBlock, i + 1);
            Kingdom kingdom = new Kingdom(i + 1, townHall);

            kingdom.setGameState(this);

            kingdoms.add(kingdom);
            absorbSurroundingBlocks(kingdom, townHallPos);
        }
    }

    private void absorbSurroundingBlocks(Kingdom kingdom, Position center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = center.getX() + dx;
                int y = center.getY() + dy;

                if (x >= 0 && x < gameMap.length && y >= 0 && y < gameMap[0].length) {
                    Block block = gameMap[x][y];
                    if (!(block instanceof VoidBlock)) {
                        kingdom.absorbBlock(block);
                    }
                }
            }
        }
    }

    public void nextTurn() {
        checkAndRemoveDestroyedStructures();

        if (isGameOver()) {
            Kingdom winner = getWinner();
            stopGame();

            if (winner != null) {
                System.out.println("🏆 GAME OVER! Winner is Kingdom #" + winner.getId());
            } else {
                System.out.println("🏴 GAME OVER! No winner (all TownHalls destroyed)");
            }
            return;
        }

        currentPlayerTurn = (currentPlayerTurn + 1) % kingdoms.size();
        if (currentPlayerTurn == 0) {
            turnNumber++;
        }

        Kingdom currentKingdom = kingdoms.get(currentPlayerTurn);
        currentKingdom.startTurn(this);

        System.out.println("Turn " + turnNumber + ": Player " + (currentPlayerTurn + 1) + "'s turn.");
    }

    public void pauseGame() {
        paused = true;
    }

    public void resumeGame() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void startGame() {
        if (running) return;

        running = true;
        scheduler = Executors.newScheduledThreadPool(2);

        resourceTask = scheduler.scheduleAtFixedRate(() -> {
            if (!running || paused) return;
            Kingdom currentKingdom = getCurrentKingdom();
            currentKingdom.updateResources();
            System.out.println("Resources updated for Player " + (currentPlayerTurn + 1));
        }, 0, RESOURCE_INTERVAL, TimeUnit.SECONDS);

        turnTask = scheduler.scheduleAtFixedRate(() -> {
            if (!running || paused) return;
            System.out.println("Turn time expired for Player " + (currentPlayerTurn + 1));
            endTurn();
        }, TURN_DURATION, TURN_DURATION, TimeUnit.SECONDS);
    }

    public void stopGame() {
        running = false;
        if (resourceTask != null) resourceTask.cancel(true);
        if (turnTask != null) turnTask.cancel(true);
        if (scheduler != null) scheduler.shutdownNow();
        System.out.println("Game stopped.");
    }

    public void endTurn() {
        nextTurn();
    }

    public int getCurrentPlayerTurn() {
        return currentPlayerTurn;
    }

    public List<Kingdom> getKingdoms() {
        return kingdoms;
    }

    public Block[][] getGameMap() {
        return gameMap;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public Kingdom getCurrentKingdom() {
        return kingdoms.get(currentPlayerTurn);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean canPlaceUnit(Unit unit) {

        return true;
    }

    public void placeUnit(Unit unit) {

    }

    public void decreaseResources(Unit unit) {
        if (unit == null) return;
        Kingdom owner = getKingdomById(unit.getKingdomId());
        if (owner != null) {
            owner.decreaseResources(unit.getGoldCost(), unit.getFoodCost());
        }
    }

    public boolean canBuildStructure(Structure structure) {
        if (structure == null) return false;
        Kingdom owner = getKingdomById(structure.getKingdomId());
        if (owner == null) return false;
        return owner.hasEnoughResources(structure.getBuildCostGold(), structure.getBuildCostFood());
    }

    public boolean canUpgradeStructure(Structure structure) {

        return true;
    }

    public void upgradeStructure(Structure structure) {

    }

    public List<Structure> getStructures() {
        return structures;
    }

    public void addStructure(Structure structure) {
        if (structure == null) return;

        structures.add(structure);
        Kingdom owner = getKingdomById(structure.getKingdomId());
        if (owner != null) {
            owner.addStructure(structure);
        }
        Block block = getBlockAt(structure.getPosition());
        if (block != null) {
            block.setStructure(structure);
        }
    }


    public void removeKingdom(Kingdom kingdom) {
        if (kingdom == null) return;


        List<Structure> structuresToRemove = new ArrayList<>(kingdom.getStructures());
        for (Structure s : structuresToRemove) {
            removeStructure(s);
        }


        kingdom.getUnits().clear();


        kingdoms.remove(kingdom);

        System.out.println("Kingdom #" + kingdom.getId() + " has been eliminated!");
    }
    public void buildStructure(Structure structure) {
        if (structure == null) return;

        Block targetBlock = getBlockAt(structure.getPosition());
        if (targetBlock == null || targetBlock instanceof VoidBlock) return;

        Kingdom owner = getKingdomById(structure.getKingdomId());
        if (owner == null || !owner.getOwnedBlocks().contains(targetBlock.getPosition().toString())) return;

        if (canBuildStructure(structure)) {
            owner.decreaseResources(structure.getBuildCostGold(), structure.getBuildCostFood());
            targetBlock.setStructure(structure);
            owner.addStructure(structure);
            System.out.println("Structure built at " + structure.getPosition());
        }
    }

    public List<Unit> getUnitsInRange(Position position, int attackRange, int kingdomId) {
        List<Unit> unitsInRange = new ArrayList<>();
        for (Kingdom kingdom : kingdoms) {
            for (Unit unit : kingdom.getUnits()) {
                if (unit.getPosition().distanceTo(position) <= attackRange && kingdom.getId() == kingdomId) {
                    unitsInRange.add(unit);
                }
            }
        }
        return unitsInRange;
    }

    public List<Unit> getAllUnits() {
        List<Unit> allUnits = new ArrayList<>();
        for (Kingdom kingdom : kingdoms) {
            allUnits.addAll(kingdom.getUnits());
        }
        return allUnits;
    }
    public boolean isGameOver() {
        int kingdomsWithTownHall = 0;
        for (Kingdom kingdom : kingdoms) {
            boolean hasTownHall = kingdom.getStructures().stream()
                    .anyMatch(s -> s instanceof TownHall && !s.isDestroyed());
            if (hasTownHall) kingdomsWithTownHall++;
        }
        return kingdomsWithTownHall <= 1;
    }

    public Kingdom getWinner() {
        if (!isGameOver()) return null;

        for (Kingdom kingdom : kingdoms) {
            boolean hasTownHall = kingdom.getStructures().stream()
                    .anyMatch(s -> s instanceof TownHall && !s.isDestroyed());
            if (hasTownHall) return kingdom;
        }
        return null;
    }


    public void saveGameState(String filePath) {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
            System.out.println("Game saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static GameState loadGameState(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            GameState loadedState = (GameState) ois.readObject();


            loadedState.scheduler = Executors.newScheduledThreadPool(2);
            loadedState.running = false;
            loadedState.paused = false;

            System.out.println("Game state loaded successfully.");
            return loadedState;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }





    public Kingdom getKingdom(int kingdomId) {
        int index = kingdomId - 1;
        if (index < 0 || index >= kingdoms.size()) {
            throw new IllegalArgumentException("Invalid kingdom ID: " + kingdomId);
        }
        return kingdoms.get(index);
    }

    public Kingdom getKingdomById(int id) {
        for (Kingdom kingdom : kingdoms) {
            if (kingdom.getId() == id) return kingdom;
        }
        return null;
    }

    public Block getBlockAt(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        if (x < 0 || x >= gameMap.length || y < 0 || y >= gameMap[0].length) {
            return null;
        }
        return gameMap[x][y];
    }

    public Unit getUnitAt(Position pos) {
        Block block = getBlockAt(pos);
        return (block != null) ? block.getUnit() : null;
    }

    public boolean isStructureAt(Position pos, int kingdomId) {
        for (Structure structure : structures) {
            if (structure.getPosition().equals(pos) && structure.getKingdomId() == kingdomId) {
                return true;
            }
        }
        return false;
    }
    public void checkAndRemoveDestroyedStructures() {
        List<Kingdom> kingdomsToRemove = new ArrayList<>();


        List<Structure> structuresToRemove = new ArrayList<>();

        for (Kingdom kingdom : kingdoms) {
            for (Structure s : kingdom.getStructures()) {
                if (s.isDestroyed()) {
                    System.out.println("Structure destroyed at " + s.getPosition());
                    structuresToRemove.add(s);


                    if (s instanceof TownHall) {
                        kingdomsToRemove.add(kingdom);
                    }
                }
            }
        }


        for (Structure s : structuresToRemove) {
            removeStructure(s);
        }


        for (Kingdom k : kingdomsToRemove) {
            removeKingdom(k);
        }


        if (kingdoms.size() == 1) {
            running = false;
            Kingdom winner = kingdoms.get(0);
            System.out.println("🏆 Game Over! Kingdom #" + winner.getId() + " wins!");
        } else if (kingdoms.size() == 0) {
            running = false;
            System.out.println("🏴 Game Over! No winners remain.");
        }
    }

    public void removeStructure(Structure structure) {
        if (structure == null) return;


        Block block = getBlockAt(structure.getPosition());
        if (block != null && block.getStructure() == structure) {
            block.removeStructure();
        }


        Kingdom owner = getKingdomById(structure.getKingdomId());
        if (owner != null) {
            owner.removeStructure(structure);
        }


        structures.remove(structure);

        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }


    public void removeDestroyedStructures() {
        List<Structure> toRemove = new ArrayList<>();
        List<Kingdom> kingdomsToRemove = new ArrayList<>();

        for (Structure s : structures) {
            if (s.isDestroyed()) {
                s.onDestroyed(this);
                toRemove.add(s);

                if (s instanceof TownHall) {
                    Kingdom owner = getKingdomById(s.getKingdomId());
                    if (owner != null && !kingdomsToRemove.contains(owner)) {
                        kingdomsToRemove.add(owner);
                    }
                }
            }
        }

        for (Structure s : toRemove) {
            Block block = getBlockAt(s.getPosition());
            if (block != null && block.getStructure() == s) {
                block.setStructure(null);
            }

            removeStructure(s);
        }

        for (Kingdom k : kingdomsToRemove) {
            for (Structure s : k.getStructures()) {
                Block b = getBlockAt(s.getPosition());
                if (b != null && b.getStructure() == s) {
                    b.setStructure(null);
                }
            }
        }


        if (gamePanel != null) {
            gamePanel.repaint();
        }

        if (kingdoms.size() == 1) {
            running = false;
            Kingdom winner = kingdoms.get(0);
            System.out.println("🏆 Game Over! Kingdom #" + winner.getId() + " wins!");
        } else if (kingdoms.size() == 0) {
            running = false;
            System.out.println("🏴 Game Over! No winners remain.");
        }
    }

    public Structure getStructureAt(Position destination) {
        for (Structure structure : structures) {
            if (structure.getPosition().equals(destination)) {
                return structure;
            }
        }
        return null;
    }

}
