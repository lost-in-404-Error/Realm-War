package org.Game.models;

import org.Game.controllers.GameController;
import org.Game.controllers.UnitController;
import org.Game.models.blocks.*;
import org.Game.models.structures.Structure;
import org.Game.models.structures.TownHall;
import org.Game.models.units.Unit;


import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.Game.models.units.Unit.gameState;

public class GameState implements Serializable {
    private int currentPlayerTurn;
    private Block[][] gameMap;
    private int turnNumber;
    private boolean running;
    private List<Structure> structures;
    private transient GameController gameController;
    private transient JPanel gamePanel;
    private List<Kingdom> kingdoms = new ArrayList<>();
    private static final long serialVersionUID = 1L;



    private transient ScheduledExecutorService scheduler;
    private transient ScheduledFuture<?> resourceTask;
    private transient ScheduledFuture<?> turnTask;

    private static final int RESOURCE_INTERVAL = 3;  // seconds
    private static final int TURN_DURATION = 30;     // seconds
    private volatile boolean paused = false;
    private boolean gameOver;
    private Kingdom winner;
    private UnitController unitController;

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

    public GameState() {

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
        if (playerCount < 1 || playerCount > 4) {
            throw new IllegalArgumentException("Player count must be between 1 and 4");
        }

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


            Player player = new Player();
            kingdom.setPlayer(player);

            kingdoms.add(kingdom);

            absorbSurroundingBlocks(kingdom, townHallPos);
        }

        evaluateGameState();
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

    public UnitController getUnitController() {
        return unitController;
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
    public boolean isGameOver() {
        return gameOver;
    }

    public Kingdom getWinner() {
        return winner;
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
    private int currentTurn = 1;

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void nextTurn() {

        evaluateGameState();
        if (!running || gameOver) return;

        checkAndRemoveDestroyedStructures();

        if (gameOver) return;

        if (kingdoms.isEmpty()) {

            gameOver = true;
            running = false;
            announceNoWinner();
            return;
        }

        currentPlayerTurn = (currentPlayerTurn + 1) % kingdoms.size();

        Kingdom currentKingdom = kingdoms.get(currentPlayerTurn);
        if (currentKingdom.hasTownHall()) {
            currentKingdom.startTurn(this);
        } else {
            nextTurn();
        }

        for (Kingdom k : gameState.getKingdoms()) {
            Player player = k.getPlayer();
            if (player != null) {
                player.calculateScore(k);
            }
        }

        turnNumber++;
    }


    public void stopGame() {
        running = false;
        if (resourceTask != null) resourceTask.cancel(true);
        if (turnTask != null) turnTask.cancel(true);
        if (scheduler != null) scheduler.shutdownNow();
        System.out.println("Game stopped.");
    }

    public void endTurn() {
        gameState.nextTurn();

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



    public Kingdom getCurrentKingdom() {
        if (kingdoms.isEmpty()) {
            return null;
        }
        if (currentPlayerTurn < 0 || currentPlayerTurn >= kingdoms.size()) {
            currentPlayerTurn = 0;
        }
        return kingdoms.get(currentPlayerTurn);
    }


    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean canPlaceUnit(Unit unit) {
        if (unit == null) return false;

        Position pos = unit.getPosition();
        Block block = getBlockAt(pos);
        Kingdom owner = getKingdomById(unit.getKingdomId());

        if (block == null || owner == null) return false;

        boolean isEmpty = block instanceof EmptyBlock && block.getUnit() == null;
        boolean isOwned = owner.getOwnedBlocks().contains(pos.toString());

        return isEmpty && isOwned;
    }

    public void placeUnit(Unit unit) {
        if (!canPlaceUnit(unit)) return;

        Block block = getBlockAt(unit.getPosition());
        if (block != null && block.getUnit() == null) {
            block.setUnit(unit);
            Kingdom owner = getKingdomById(unit.getKingdomId());
            if (owner != null) {
                owner.getUnits().add(unit);
            }
            System.out.println("✅ Unit placed at " + unit.getPosition());
        }
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

        int removedIndex = kingdoms.indexOf(kingdom);
        kingdoms.remove(kingdom);

        if (removedIndex <= currentPlayerTurn && currentPlayerTurn > 0) {
            currentPlayerTurn--;
        }

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
            for (Structure s : new ArrayList<>(kingdom.getStructures())) {
                if (s.isDestroyed()) {
                    System.out.println("💥 Structure destroyed at " + s.getPosition());
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


        evaluateGameState();
    }


    public void checkGameOver() {
        List<Kingdom> alive = kingdoms.stream().filter(Kingdom::hasTownHall).toList();

        if (alive.size() <= 1) {
            gameOver = true;
            winner = alive.isEmpty() ? null : alive.get(0);
        } else {

            if (checkVictoryByBlocks()) {
                gameOver = true;
            }
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

        evaluateGameState();
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



    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public GameController getGameController() {
        return gameController;
    }
    private void announceWinner(Kingdom winner) {
        System.out.println("🏁 Game Over!");
        System.out.println("🏆 Kingdom #" + winner.getId() + " wins!");

        if (gameController != null && gameController.getGamePanel() != null) {
            //  JOptionPane.showMessageDialog(gameController.getGamePanel(),
            //        "🏆 Player with ID " + winner.getId() + " wins the game!");
        }
    }
    private void announceNoWinner() {
        System.out.println("🟨 Game Over! No winners.");
        if (gameController != null && gameController.getGamePanel() != null) {
            //  JOptionPane.showMessageDialog(gameController.getGamePanel(),
            //        "🟨 Game Over! No players left standing.");
        }
    }



    public boolean checkVictoryByBlocks() {
        Kingdom currentKingdom = getCurrentKingdom();
        int currentId = currentKingdom.getId();

        List<Kingdom> eliminated = new ArrayList<>();

        for (Kingdom opponent : new ArrayList<>(kingdoms)) {
            if (opponent.getId() == currentId) continue;

            boolean hasAnyBlock = false;

            for (int x = 0; x < gameMap.length && !hasAnyBlock; x++) {
                for (int y = 0; y < gameMap[0].length; y++) {
                    Block block = gameMap[x][y];
                    if (block.getKingdomId() == opponent.getId()) {
                        hasAnyBlock = true;
                        break;
                    }
                }
            }

            if (!hasAnyBlock) {
                eliminated.add(opponent);
            }
        }

        if (!eliminated.isEmpty()) {
            for (Kingdom k : eliminated) {
                removeKingdom(k);
            }

            winner = currentKingdom;
            gameOver = true;
            running = false;

            announceWinner(currentKingdom);

            return true;
        }

        return false;
    }




    public void evaluateGameState() {
        if (gameOver) return;


        if (checkVictoryByBlocks()) {
            return;
        }


        List<Kingdom> alive = kingdoms.stream()
                .filter(Kingdom::hasTownHall)
                .toList();

        if (alive.size() == 1) {
            winner = alive.get(0);
            gameOver = true;
            running = false;
            announceWinner(winner);
        } else if (alive.isEmpty()) {
            gameOver = true;
            running = false;
            announceNoWinner();
        }
    }


    public void setGamePanel(JPanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void setKingdoms(List<Kingdom> kingdoms) {
    }

    public int getTurnNumber() {
        return turnNumber;
    }
}