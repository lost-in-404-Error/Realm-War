package org.Game.models.structures;

import org.Game.models.GameState;
import org.Game.models.Kingdom;
import org.Game.models.Position;
import org.Game.models.blocks.Block;

public class Market extends Structure {

    private static final int[] GOLD_PRODUCTION_BY_LEVEL = {5, 10, 15};
    private static final int[] DURABILITY_BY_LEVEL = {50, 75, 100};
    private static final int[] UPGRADE_COST_BY_LEVEL = {5, 10, 15};

    private static final int BASE_BUILDING_COST = 5;
    private static final int MAINTENANCE_COST = 5;
    private static final int MAX_LEVEL = 3;

    private int goldProduction;
    private final int buildCostGold;

    public Market(Position position, Block baseBlock, int kingdomId, int existingMarketsCount) {
        super(MAX_LEVEL, DURABILITY_BY_LEVEL[0], MAINTENANCE_COST, position, baseBlock, kingdomId);
        setLevel(1);
        this.goldProduction = GOLD_PRODUCTION_BY_LEVEL[0];
        this.buildCostGold = getBuildingCost(existingMarketsCount);
    }

    @Override
    public boolean canUpgrade() {
        return getLevel() < MAX_LEVEL;
    }

    @Override
    public void upgrade() {
        if (!canUpgrade()) {
            throw new IllegalStateException("Market is already at max level.");
        }
        int newLevel = getLevel() + 1;
        setLevel(newLevel);
        setDurability(DURABILITY_BY_LEVEL[newLevel - 1]);
        this.goldProduction = GOLD_PRODUCTION_BY_LEVEL[newLevel - 1];
    }

    @Override
    public void performTurnAction(Kingdom kingdom, GameState gameState) {
        kingdom.addGold(goldProduction);
    }

    public int getGoldProduction() {
        return goldProduction;
    }

    public int getUpgradeCost() {
        return UPGRADE_COST_BY_LEVEL[getLevel() - 1];
    }

    public static int getBuildingCost(int existingMarkets) {
        return BASE_BUILDING_COST + existingMarkets * 5;
    }

    @Override
    public int getBuildCostGold() {
        return buildCostGold;
    }

    @Override
    public int getBuildCostFood() {
        return 0;
    }

    @Override
    public int getUnitSpace() {
        return 0;
    }
}
