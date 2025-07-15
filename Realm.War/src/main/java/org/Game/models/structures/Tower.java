package org.Game.models.structures;

import org.Game.models.GameState;
import org.Game.models.Kingdom;
import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.units.Unit;

import java.util.List;

public abstract class Tower extends Structure {
    private static final int MAX_LEVEL = 3;
    private static final int[] ATTACK_POWER_BY_LEVEL = {10, 15, 20};
    private static final int[] DURABILITY_BY_LEVEL = {100, 150, 200};
    private static final int[] MAINTENANCE_COST_BY_LEVEL = {10, 15, 20};
    private static final int[] LEVEL_UP_COST_BY_LEVEL = {10, 20, 30};
    private static final int BASE_BUILDING_COST = 10;

    private final int attackRange = 3;
    private int attackPower;

    public Tower(Position position, Block baseBlock, int kingdomId) {
        super(MAX_LEVEL, DURABILITY_BY_LEVEL[0], MAINTENANCE_COST_BY_LEVEL[0], position, baseBlock, kingdomId);
        this.attackPower = ATTACK_POWER_BY_LEVEL[0];
        setLevel(1);
    }

    @Override
    public boolean canUpgrade() {
        return getLevel() < MAX_LEVEL;
    }

    @Override
    public void upgrade() {
        if (!canUpgrade()) {
            throw new IllegalStateException("Tower is already at max level");
        }
        int newLevel = getLevel() + 1;
        setLevel(newLevel);
        setDurability(DURABILITY_BY_LEVEL[newLevel - 1]);
        this.attackPower = ATTACK_POWER_BY_LEVEL[newLevel - 1];

        setMaintenanceCost(MAINTENANCE_COST_BY_LEVEL[newLevel - 1]);
    }

    private void setMaintenanceCost(int i) {

    }

    @Override
    public void performTurnAction(Kingdom kingdom, GameState gameState) {

        List<Unit> enemies = gameState.getUnitsInRange(getPosition(), attackRange, kingdom.getId());
        for (Unit enemy : enemies) {
            enemy.takeDamage(attackPower);
        }
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getLevelUpCost() {
        return LEVEL_UP_COST_BY_LEVEL[getLevel() - 1];
    }

    public static int getBuildingCost(int towersCount) {
        return BASE_BUILDING_COST + (towersCount * 10);
    }
}
