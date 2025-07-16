package org.Game.models.units;

import org.Game.models.Position;

public class Peasant extends Unit {
    private static final int  HEALTH= 30;
    private static final int MOVEMENT = 2;
    private static final int ATTACK_POWER = 5;
    private static final int ATTACK_RANGE = 1;
    private static final int GOLD_COST = 2;
    private static final int FOOD_COST = 1;
    private static final int UNIT_SPACE = 1;
    private static final int HP=30;
    private static final int MAX_HP=30 ;
    public Peasant(Position position, int kingdomId) {
        super("Peasant",
                HEALTH,
                MOVEMENT,
                ATTACK_POWER,
                ATTACK_RANGE,
                HP,
                MAX_HP,
                GOLD_COST,
                position,
                kingdomId,
                MOVEMENT);

        this.foodCost = FOOD_COST;
        this.unitSpace = UNIT_SPACE;
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
