package org.Game.models;

import org.Game.models.blocks.*;
import org.Game.models.structures.Structure;
import org.Game.models.structures.TownHall;
import org.Game.models.units.Unit;

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

    private transient ScheduledExecutorService scheduler;
    private transient ScheduledFuture<?> resourceTask;
    private transient ScheduledFuture<?> turnTask;

    private static final int RESOURCE_INTERVAL = 3;  // seconds
    private static final int TURN_DURATION = 30;     // seconds

    public GameState(int mapWidth, int mapHeight, int playerCount) {
        this.kingdoms = new ArrayList<>();
        this.gameMap = new Block[mapWidth][mapHeight];
        this.currentPlayerTurn = 0;
        this.turnNumber = 1;
        this.running = false;

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
        currentPlayerTurn = (currentPlayerTurn + 1) % kingdoms.size();

        if (currentPlayerTurn == 0) {
            turnNumber++;
        }

        Kingdom currentKingdom = kingdoms.get(currentPlayerTurn);
        currentKingdom.startTurn();
        System.out.println("Turn " + turnNumber + ": Player " + (currentPlayerTurn + 1) + "'s turn.");
    }

    public void startGame() {
        if (running) return;

        running = true;
        scheduler = Executors.newScheduledThreadPool(2);

        resourceTask = scheduler.scheduleAtFixedRate(() -> {
            if (!running) return;
            Kingdom currentKingdom = getCurrentKingdom();
            currentKingdom.updateResources();
            System.out.println("Resources updated for Player " + (currentPlayerTurn + 1));
        }, 0, RESOURCE_INTERVAL, TimeUnit.SECONDS);

        turnTask = scheduler.scheduleAtFixedRate(() -> {
            if (!running) return;
            System.out.println("Turn time expired for Player " + (currentPlayerTurn + 1));
            endTurn();
        }, TURN_DURATION, TURN_DURATION, TimeUnit.SECONDS);

        kingdoms.get(currentPlayerTurn).startTurn();
        System.out.println("Game started. Player " + (currentPlayerTurn + 1) + " begins.");
    }

    public void stopGame() {
        running = false;
        if (resourceTask != null) resourceTask.cancel(true);
        if (turnTask != null) resourceTask.cancel(true);
        if (scheduler != null) scheduler.shutdownNow();
        System.out.println("Game stopped.");
    }

    public void endTurn() {
        nextTurn();
    }


    public int getCurrentPlayerTurn() { return currentPlayerTurn; }
    public List<Kingdom> getKingdoms() { return kingdoms; }
    public Block[][] getGameMap() { return gameMap; }
    public int getTurnNumber() { return turnNumber; }
    public Kingdom getCurrentKingdom() { return kingdoms.get(currentPlayerTurn); }
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }


    public boolean canPlaceUnit(Unit u) {

        return true;
    }

    public void placeUnit(Unit u) {

    }

    public void decreaseResources(Unit u) {

    }

    public boolean canBuildStructure(Structure s) {

        return true;
    }

    public boolean canUpgradeStructure(Structure s) {

        return true;
    }

    public void upgradeStructure(Structure s) {

    }

    public void buildStructure(Structure s) {

    }

    public List<Unit> getUnitsInRange(Position position, int attackRange, int id) {

        return new ArrayList<>();
    }


    public void saveGameState(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
            System.out.println("Game state saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState loadGameState(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            GameState gameState = (GameState) ois.readObject();


            gameState.scheduler = Executors.newScheduledThreadPool(2);
            gameState.running = false;

            System.out.println("Game state loaded from " + filePath);
            return gameState;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
