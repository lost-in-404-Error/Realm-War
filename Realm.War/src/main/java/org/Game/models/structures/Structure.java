package org.Game.models.structures;

import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.GameState;
import org.Game.models.Kingdom;

import java.io.Serializable;

public abstract class Structure implements Serializable {
    private static final long serialVersionUID = 1L;

    private int level;
    private final int maxLevel;
    private int durability;
    private final int maintenanceCost;
    private final Position position;
    private final Block baseBlock;
    private int kingdomId;


    transient private Kingdom kingdom;

    public Structure(int maxLevel, int initialDurability, int maintenanceCost,
                     Position position, Block baseBlock, int kingdomId) {
        this.level = 1;
        this.maxLevel = maxLevel;
        this.durability = initialDurability;
        this.maintenanceCost = maintenanceCost;
        this.position = position;
        this.baseBlock = baseBlock;
        this.kingdomId = kingdomId;
    }


    public abstract int getBuildCostGold();
    public abstract int getBuildCostFood();
    public abstract int getUnitSpace();
    public abstract boolean canUpgrade();
    public abstract void upgrade();
    public abstract void performTurnAction(Kingdom kingdom, GameState gameState);


    public int getLevel() {
        return level;
    }

    protected void setLevel(int level) {
        if (level > 0 && level <= maxLevel) {
            this.level = level;
        }
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = Math.max(0, durability);
    }

    public int getMaintenanceCost() {
        return maintenanceCost;
    }

    public Position getPosition() {
        return position;
    }

    public Block getBaseBlock() {
        return baseBlock;
    }

    public int getKingdomId() {
        return kingdomId;
    }

    public void setKingdomId(int kingdomId) {
        this.kingdomId = kingdomId;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public String getType() {
        return "structure";
    }


    public int getTotalBuildCost() {
        return getBuildCostGold() + getBuildCostFood() + getUnitSpace();
    }


    public boolean isDestroyed() {
        return durability <= 0;
    }


    public void takeDamage(int damage) {
        setDurability(this.durability - damage);
    }


    public void onDestroyed(GameState gameState) {
        Block block = gameState.getBlockAt(getPosition());
        if (block != null) {
            block.removeStructure();
        }

        Kingdom kingdom = gameState.getKingdomById(getKingdomId());
        if (kingdom != null) {
            kingdom.removeStructure(this);


            if (this instanceof TownHall) {
                kingdom.setTownHall(null);
            }
        }
    }



    public void setDestroyed(boolean b) {
    }


}
