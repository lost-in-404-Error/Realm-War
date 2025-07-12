package org.Game.models.units;

import org.Game.models.Position;

public class Swordman extends Unit {
    public static final int SWORDSMAN_HP = 80;
    public static final int MOVEMENT = 2;
    public static final int ATTACK_POWER = 10;
    public static final int ATTACK_RANGE = 1;
    public static final int GOLD_COST = 4;
    public static final int FOOD_COST = 2;
    public static final int UNIT_SPACE = 2;

    public Swordman(Position position, int kingdomId) {
        super("Swordman", SWORDSMAN_HP, MOVEMENT, ATTACK_POWER, ATTACK_RANGE,
                GOLD_COST, FOOD_COST, UNIT_SPACE, position, kingdomId);
    }

    @Override
    public boolean canMerge(Unit other) {
        return other instanceof Swordman && this.getKingdomId() == other.getKingdomId();
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