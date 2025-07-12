package org.Game.models.structures;


import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.GameState;
import org.Game.models.Kingdom;

public class Barrack extends Structure {

    private static final int[] UNIT_SPACE_BY_LEVEL = {5, 8, 12};
    private static final int[] DURABILITY_BY_LEVEL = {50, 75, 100};
    private static final int[] UPGRADE_COST_BY_LEVEL = {5, 10, 15};

    private static final int MAINTENANCE_COST = 5;
    private static final int MAX_LEVEL = 3;

    private int unitSpace;

    public Barrack(Position position, Block baseBlock, int kingdomId) {
        super(MAX_LEVEL, DURABILITY_BY_LEVEL[0], MAINTENANCE_COST, position, baseBlock, kingdomId);
        setLevel(1);
        this.unitSpace = UNIT_SPACE_BY_LEVEL[0];
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

        int newLevel = getLevel() + 1;
        setLevel(newLevel);
        setDurability(DURABILITY_BY_LEVEL[newLevel - 1]);
        this.unitSpace = UNIT_SPACE_BY_LEVEL[newLevel - 1];
    }

    public int getUnitSpace() {
        return unitSpace;
    }

    public int getUpgradeCost() {
        return UPGRADE_COST_BY_LEVEL[getLevel() - 1];
    }

    public static int getBuildingCost(int existingBarracks) {
        return 5 + existingBarracks * 5;
    }

    @Override
    public void performTurnAction(Kingdom kingdom, GameState gameState) {
        if (kingdom.canTrainUnit()) {
            kingdom.trainUnit();  // تو باید این متدها رو در Kingdom تعریف کنی
        }
    }
}
