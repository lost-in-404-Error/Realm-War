package org.Game.models.units;

import org.Game.models.Position;

public class Knight extends Unit {
    private static final int HEALTH= 120;
    private static final int SPEED = 3;
    private static final int ATTACK_POWER = 25;
    private static final int ATTACK_RANGE = 1;
    private static final int GOLD_COST = 12;
    private static final int FOOD_COST = 0;
    private static final int UNIT_SPACE = 6;
    private static final int HP=120;
    private static final int MAX_HP= 120;
    public Knight(Position position, int kingdomId) {
        super("Knight",
                HEALTH,
                SPEED,
                ATTACK_POWER,
                ATTACK_RANGE,
                HP, // hitPoints
                MAX_HP, // maxHitPoints
                GOLD_COST,
                position,
                kingdomId,
                SPEED);

        this.foodCost = FOOD_COST;
        this.unitSpace = UNIT_SPACE;
    }

    @Override
    public boolean canMerge(Unit other) {
        return false;
    }

    @Override
    public Unit merge(Unit other) {
        throw new UnsupportedOperationException("Knight cannot be merged.");
    }

    @Override
    public boolean canMergeWith(Unit unit2) {
        return false;
    }
}
