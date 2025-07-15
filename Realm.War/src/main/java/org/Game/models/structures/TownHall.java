package org.Game.models.structures;

import org.Game.models.GameState;
import org.Game.models.Kingdom;
import org.Game.models.Position;
import org.Game.models.blocks.Block;

public class TownHall extends Structure {
    private static final int MAX_LEVEL = 1;
    private static final int INITIAL_DURABILITY = 50;
    private static final int MAINTENANCE_COST = 0;
    private static final int GOLD_PRODUCTION = 5;
    private static final int FOOD_PRODUCTION = 5;
    private static final int UNIT_SPACE = 5;
    private int kingdomId;

    public TownHall(Position position, Block baseBlock, int kingdomId) {
        super(MAX_LEVEL, INITIAL_DURABILITY, MAINTENANCE_COST, position, baseBlock, kingdomId);
    }

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {
        throw new UnsupportedOperationException("TownHall cannot be upgraded.");
    }

    @Override
    public void performTurnAction(Kingdom kingdom, GameState gameState) {
        kingdom.addGold(GOLD_PRODUCTION);
        kingdom.addFood(FOOD_PRODUCTION);
    }


    public int getGoldProduction() {
        return GOLD_PRODUCTION;
    }


    public int getFoodProduction() {
        return FOOD_PRODUCTION;
    }

    @Override
    public int getUnitSpace() {
        return UNIT_SPACE;
    }

    @Override
    public int getBuildCostGold() {
        return 0;
    }

    @Override
    public int getBuildCostFood() {
        return 0;
    }

    public void setKingdomId(int kingdomId) {
        this.kingdomId = kingdomId;
    }

    public int getKingdomId() {
        return kingdomId;
    }
}
