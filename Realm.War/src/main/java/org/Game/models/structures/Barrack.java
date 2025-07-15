package org.Game.models.structures;

import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.GameState;
import org.Game.models.Kingdom;

public class Barrack extends Structure {

    private static final int[] UNIT_SPACE_BY_LEVEL = {5, 8, 12};
    private static final int[] DURABILITY_BY_LEVEL = {50, 75, 100};
    private static final int[] LEVEL_UP_COST_BY_LEVEL = {5, 10, 15};

    private static final int BASE_BUILDING_COST = 5;
    private static final int MAINTENANCE_COST = 5;
    private static final int MAX_LEVEL = 3;

    private int unitSpace;
    private final int buildCostGold;

    public Barrack(Position position, Block baseBlock, int kingdomId, int existingBarracksCount) {
        super(MAX_LEVEL, DURABILITY_BY_LEVEL[0], MAINTENANCE_COST, position, baseBlock, kingdomId);
        setLevel(1);
        this.unitSpace = UNIT_SPACE_BY_LEVEL[0];
        this.buildCostGold = getBuildingCost(existingBarracksCount);
    }

    @Override
    public boolean canUpgrade() {
        return getLevel() < MAX_LEVEL;
    }

    @Override
    public void upgrade() {
        if (!canUpgrade()) {
            throw new IllegalStateException("Barrack is already at max level.");
        }

        int nextLevel = getLevel() + 1;
        setLevel(nextLevel);
        setDurability(DURABILITY_BY_LEVEL[nextLevel - 1]);
        this.unitSpace = UNIT_SPACE_BY_LEVEL[nextLevel - 1];
    }

    @Override
    public void performTurnAction(Kingdom kingdom, GameState gameState) {

    }

    @Override
    public int getUnitSpace() {
        return unitSpace;
    }

    @Override
    public int getBuildCostGold() {
        return buildCostGold;
    }

    @Override
    public int getBuildCostFood() {
        return 0;
    }

    public int getUpgradeCost() {
        return LEVEL_UP_COST_BY_LEVEL[getLevel() - 1];
    }

    public static int getBuildingCost(int existingBarracksCount) {
        return BASE_BUILDING_COST + (existingBarracksCount * 5);
    }
}
