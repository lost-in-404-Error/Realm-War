package org.Game.models.structures;

import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.GameState;
import org.Game.models.Kingdom;

public abstract class Structure {
    private int level;
    private final int maxLevel;
    private int durability;
    private final int maintenanceCost;
    private final Position position;
    private final Block baseBlock;
    private int kingdomId;

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

    // Abstract Methods
    public abstract int getBuildCostGold();
    public abstract int getBuildCostFood();
    public abstract int getUnitSpace();
    public abstract boolean canUpgrade();
    public abstract void upgrade();
    public abstract void performTurnAction(Kingdom kingdom, GameState gameState);

    // Core Getters/Setters
    public int getLevel() {
        return level;
    }

    protected void setLevel(int level) {
        this.level = level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
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

    public void setKingdomId(int id) {
        this.kingdomId = id;
    }

    public int getKingdomID() {
        return kingdomId;
    }

    public String getType() {
        return "structure";
    }

    public Object getCost() {
        return getBuildCostGold() + getBuildCostFood() + getUnitSpace();
    }

    // ========== 🔥 New Logic: Destruction ==========
    public boolean isDestroyed() {
        return durability <= 0;
    }

    public void takeDamage(int damage) {
        this.durability -= damage;
        if (this.durability <= 0) {
            this.durability = 0;
        }
    }

    public void onDestroyed(GameState gameState) {

        Block block = gameState.getBlockAt(getPosition());
        if (block != null) {
            block.removeStructure();
        }


        Kingdom kingdom = gameState.getKingdomById(getKingdomId());
        if (kingdom != null) {
            kingdom.removeStructure(this);
        }
    }

}
