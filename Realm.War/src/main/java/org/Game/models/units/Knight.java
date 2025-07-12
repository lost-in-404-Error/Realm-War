package org.Game.models.units;

import org.Game.models.Position;

public class Knight extends Unit {
    private static final int KNIGHT_HP = 120;
    private static final int SPEED = 3;
    private static final int ATTACK_POWER = 25;
    private static final int ATTACK_RANGE = 1;
    private static final int GOLD_COST = 12;
    private static final int FOOD_COST = 0;
    private static final int UNIT_SPACE = 6;

    public Knight(Position position, int kingdomId) {
        super("Knight", KNIGHT_HP, SPEED, ATTACK_POWER, ATTACK_RANGE,
                GOLD_COST, FOOD_COST, UNIT_SPACE, position, kingdomId);
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