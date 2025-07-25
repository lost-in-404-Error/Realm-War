package org.Game.models;

import org.Game.models.blocks.Block;
import org.Game.models.structures.Structure;
import org.Game.models.structures.TownHall;
import org.Game.models.units.Unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Kingdom implements Serializable {
    private final int id;
    private int gold;
    private int food;
    private int totalUnitSpace;
    private int usedUnitSpace;
    private  TownHall townHall;
    private List<Structure> structures;
    private List<Unit> units;
    private  List<Block> absorbedBlocks;
    private GameState gameState;
    private String playerName;


    public Kingdom(int id, TownHall townHall) {
        this.id = id;
        this.townHall = townHall;
        this.structures = new ArrayList<>();
        this.units = new ArrayList<>();
        this.absorbedBlocks = new ArrayList<>();

        if (this.townHall != null) {
            this.townHall.setKingdomId(id);
            this.structures.add(this.townHall);
            this.totalUnitSpace = this.townHall.getUnitSpace();
        } else {
            this.totalUnitSpace = 0;
        }

        this.usedUnitSpace = 0;
        this.gold =100;
        this.food = 50;
    }


    public void startTurn(GameState gameState) {
        this.gameState = gameState;
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
        structures.remove(structure);
    }


    public void addUnit(Unit unit) {
        if (usedUnitSpace + unit.getUnitSpace() > totalUnitSpace) {
            throw new IllegalStateException("Not enough unit space");
        }
        units.add(unit);
        usedUnitSpace += unit.getUnitSpace();
    }



    public int countStructuresOfType(Class<? extends Structure> type) {
        return (int) structures.stream().filter(type::isInstance).count();
    }

    public void updateResources() {
        this.gold += 5;
        this.food += 3;
    }



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

    public void setTownHall(TownHall townHall) {
        this.townHall = townHall;
        if (townHall != null) {
            townHall.setKingdomId(this.id);
            if (!structures.contains(townHall)) {
                structures.add(townHall);
            }
            this.totalUnitSpace += townHall.getUnitSpace();
        }
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



    public void decreaseResources(int goldAmount, int foodAmount) {
        decreaseGold(goldAmount);
        decreaseFood(foodAmount);
    }

    public boolean hasEnoughResources(int goldAmount, int foodAmount) {
        return this.gold >= goldAmount && this.food >= foodAmount;
    }

    public void removeUnit(Unit targetUnit) {
        if (targetUnit != null && units.remove(targetUnit)) {
            usedUnitSpace -= targetUnit.getUnitSpace();
        }
    }
    public void moveUnit(Unit unit, Position newPosition) {
        Block newBlock = gameState.getBlockAt(newPosition);
        if (newBlock != null) {
            Kingdom ownerKingdom = unit.getKingdom();
            ownerKingdom.absorbBlock(newBlock);

            unit.setPosition(newPosition);
        }
    }




    public String getOwnedBlocks() {
        StringBuilder sb = new StringBuilder();
        for (Block block : absorbedBlocks) {
            sb.append(block.getPosition().toString()).append(" ");
        }
        return sb.toString().trim();
    }

    public void decreaseResources(Object cost) {
        if (cost instanceof Structure) {
            Structure structure = (Structure) cost;
            this.gold -= structure.getBuildCostGold();
            this.food -= structure.getBuildCostFood();
        } else if (cost instanceof Unit) {
            Unit unit = (Unit) cost;
            this.gold -= unit.getGoldCost();
            this.food -= unit.getFoodCost();
        }
    }
    public boolean hasUnitsOrStructures() {
        return !units.isEmpty() || !structures.isEmpty();
    }
    public void setOwnedBlocksFromString(String data) {
        if (data == null || data.isEmpty()) return;
        String[] parts = data.split(",");
        for (String part : parts) {

        }
    }

    public void absorbBlock(Block block) {

        if (block == null) return;

        int oldOwnerId = block.getOwnerID();
        if (oldOwnerId == this.id) return;


        if (oldOwnerId != -1) {
            Kingdom oldOwner = gameState.getKingdomById(oldOwnerId);
            if (oldOwner != null) {
                oldOwner.getAbsorbedBlocks().remove(block);

                Structure structure = block.getStructure();
                if (structure != null) {
                    structure.setDestroyed(true);
                    oldOwner.removeStructure(structure);
                    block.removeStructure();


                    if (structure instanceof TownHall) {
                        TownHall townHall = oldOwner.getTownHall();
                        if (townHall != null) {
                            townHall.setDestroyed(true);
                            oldOwner.setTownHall(null);
                            System.out.println("🏳️ Player " + oldOwner.getId() + " has been defeated!");
                        }
                    }

                    if (gameState != null) {
                        gameState.evaluateGameState();
                    }
                }

            }
        }


        block.setAbsorbed(true, this.id);
        absorbedBlocks.add(block);
    }

    public boolean isDefeated() {
        return townHall == null || townHall.isDestroyed();
    }

    public void setTownHall(Object o) {
        if (o == null) {
            this.townHall = null;
        } else if (o instanceof TownHall) {
            this.setTownHall((TownHall) o);
        }
    }


    public boolean hasTownHall() {
        return structures.stream()
                .anyMatch(s -> s instanceof TownHall && !s.isDestroyed());
    }

    public TownHall getTownHall() {
        for (Structure s : structures) {
            if (s instanceof TownHall) {
                return (TownHall) s;
            }
        }
        return null;
    }
    private Player player;

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }




    public void setStructures(List<Structure> structures) {
    }

    public boolean createUnit(Unit unit) {
        return units.add(unit);
    }

    public void setId(int kingdomId) {
    }


    public void setOwnedBlocks(String ownedBlocks) {

    }
}