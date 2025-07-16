package org.Game.models;

import org.Game.models.blocks.Block;
import org.Game.models.structures.Structure;
import org.Game.models.structures.TownHall;
import org.Game.models.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Kingdom {
    private final int id;
    private int gold;
    private int food;
    private int totalUnitSpace;
    private int usedUnitSpace;
    private final TownHall townHall;
    private final List<Structure> structures;
    private final List<Unit> units;
    private final List<Block> absorbedBlocks;
    private GameState gameState;

    public Kingdom(int id, TownHall townHall) {
        this.id = id;
        this.townHall = townHall;
        this.structures = new ArrayList<>();
        this.units = new ArrayList<>();
        this.absorbedBlocks = new ArrayList<>();
        this.townHall.setKingdomId(id);
        this.structures.add(townHall);
        this.totalUnitSpace = townHall.getUnitSpace();
        this.usedUnitSpace = 0;
        this.gold = 20;
        this.food = 20;
    }

    public void startTurn(GameState gameState) {
        for (Structure structure : structures) {
            structure.performTurnAction(this, this.gameState);
        }

        for (Block block : absorbedBlocks) {
            this.gold += block.getResourceYield("GOLD");
            this.food += block.getResourceYield("FOOD");
        }

        for (Structure structure : structures) {
            this.gold -= structure.getMaintenanceCost();
        }

        for (Unit unit : units) {
            this.gold -= unit.getPaymentCost();
            this.food -= unit.getRationCost();
        }
    }

    public void addStructure(Structure structure) {
        if (structure != null) {
            structure.setKingdomId(this.id);
            structures.add(structure);
            totalUnitSpace += structure.getUnitSpace();
        }
    }

    public void removeStructure(Structure structure) {
        if (structures.remove(structure)) {
            totalUnitSpace -= structure.getUnitSpace();
        }
    }

    public void addUnit(Unit unit) {
        if (usedUnitSpace + unit.getUnitSpace() > totalUnitSpace) {
            throw new IllegalStateException("Not enough unit space");
        }
        units.add(unit);
        usedUnitSpace += unit.getUnitSpace();
    }

    public void absorbBlock(Block block) {
        block.setAbsorbed(true, this.id);
        absorbedBlocks.add(block);
    }

    public int countStructuresOfType(Class<? extends Structure> type) {
        return (int) structures.stream().filter(type::isInstance).count();
    }

    public void updateResources() {
        this.gold += 5;
        this.food += 3;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getTotalUnitSpace() {
        return totalUnitSpace;
    }

    public void setTotalUnitSpace(int totalUnitSpace) {
        this.totalUnitSpace = totalUnitSpace;
    }

    public int getUsedUnitSpace() {
        return usedUnitSpace;
    }

    public TownHall getTownHall() {
        return townHall;
    }

    public List<Structure> getStructures() {
        return structures;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<Block> getAbsorbedBlocks() {
        return absorbedBlocks;
    }

    public void addGold(int amount) {
        this.gold += amount;
    }

    public void addFood(int amount) {
        this.food += amount;
    }

    public void decreaseGold(int amount) {
        this.gold = Math.max(0, this.gold - amount);
    }

    public void decreaseFood(int amount) {
        this.food = Math.max(0, this.food - amount);
    }

    public void subtractGold(int amount) {
        decreaseGold(amount);
    }

    public void subtractFood(int amount) {
        decreaseFood(amount);
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Kingdom getUnitController() {

        return this;
    }
}
