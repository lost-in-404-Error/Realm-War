package org.Game.models.units;

import org.Game.models.Position;

public class Peasant extends Unit {
    private static final int PEASANT_HP  = 30;
    private static final int MOVEMENT = 2;
    private static final int ATTACK_POWER = 5;
    private static final int ATTACK_RANGE = 1;
    private static final int GOLD_COST = 2;
    private static final int FOOD_COST = 1;
    private static final int UNIT_SPACE = 1;

    public Peasant(Position position, int kingdomId) {
        super("Peasant", PEASANT_HP, MOVEMENT, ATTACK_POWER, ATTACK_RANGE,
                GOLD_COST, FOOD_COST, UNIT_SPACE, position, kingdomId);
    }

    @Override
    public boolean canMerge(Unit other) {
        return other instanceof Peasant && this.getKingdomId() == other.getKingdomId();
    }

    @Override
    public Unit merge(Unit other) {
        if (!canMerge(other)) {
            throw new IllegalArgumentException("Cannot merge these units");
        }
        return new Spearman(this.getPosition(), this.getKingdomId());
    }
    @Override
    public boolean canMergeWith(Unit unit2) {
        return canMerge(unit2);
    }
}