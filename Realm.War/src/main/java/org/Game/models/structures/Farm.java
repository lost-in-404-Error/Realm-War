package org.Game.models.structures;

import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.GameState;
import org.Game.models.Kingdom;

public class Farm extends Structure {

    private static final int[] FOOD_PRODUCTION_BY_LEVEL = {5, 10, 15};
    private static final int[] DURABILITY_BY_LEVEL = {40, 55, 70};
    private static final int[] LEVEL_UP_COST_BY_LEVEL = {5, 10, 15};
    private static final int BASE_BUILDING_COST = 5;
    private static final int MAINTENANCE_COST = 5;
    private static final int MAX_LEVEL = 3;

    private int foodProduction;

    public Farm(Position position, Block baseBlock, int kingdomId, int existingFarmsCount) {
        super(MAX_LEVEL, DURABILITY_BY_LEVEL[0], MAINTENANCE_COST, position, baseBlock, kingdomId);
        setLevel(1);
        this.foodProduction = FOOD_PRODUCTION_BY_LEVEL[0];
    }

    @Override
    public boolean canUpgrade() {
        return getLevel() < MAX_LEVEL;
    }

    @Override
    public void upgrade() {
        if (!canUpgrade()) {
            throw new IllegalStateException("Farm is already at max level.");
        }

        int nextLevel = getLevel() + 1;
        setLevel(nextLevel);
        setDurability(DURABILITY_BY_LEVEL[nextLevel - 1]);
        this.foodProduction = FOOD_PRODUCTION_BY_LEVEL[nextLevel - 1];
    }

    @Override
    public void performTurnAction(Kingdom kingdom, GameState gameState) {
        kingdom.addFood(foodProduction);
    }

    public int getFoodProduction() {
        return foodProduction;
    }

    public int getUpgradeCost() {
        return LEVEL_UP_COST_BY_LEVEL[getLevel() - 1];
    }

    public static int getBuildingCost(int farmsCount) {
        return BASE_BUILDING_COST + (farmsCount * 5);
    }
}
