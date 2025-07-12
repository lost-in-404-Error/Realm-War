package org.Game.models.units;

import org.Game.models.Position;

public class Spearman extends Unit {
    public static final int SPEARMAN_HP = 30;
    public static final int MOVEMENT = 1;
    public static final int ATTACK_POWER = 5;
    public static final int ATTACK_RANGE = 1;
    public static final int GOLD_COST = 2;
    public static final int FOOD_COST = 1;
    public static final int UNIT_SPACE = 1;

    public Spearman(Position position, int kingdomId) {
        super("Spearman", SPEARMAN_HP, MOVEMENT, ATTACK_POWER, ATTACK_RANGE,
                GOLD_COST, FOOD_COST, UNIT_SPACE, position, kingdomId);
    }

    @Override
    public boolean canMerge(Unit other) {
        return other instanceof Spearman && this.getKingdomId() == other.getKingdomId();
    }

    @Override
    public Unit merge(Unit other) {
        if (!canMerge(other)) {
            throw new IllegalArgumentException("Cannot merge these units");
        }
        return new Knight(this.getPosition(), this.getKingdomId());
    }

    @Override
    public boolean canMergeWith(Unit unit2) {
        return canMerge(unit2);
    }
}