package org.Game.models.units;

import org.Game.models.Position;

public class Spearman extends Unit {
    private static final int HEALTH = 30;
    private static final int SPEED = 1;
    private static final int ATTACK = 5;
    private static final int RANGE = 1;
    private static final int HP = 30;
    private static final int MAX_HP = 30;
    private static final int GOLD_COST = 2;
    private static final int FOOD_COST = 1;
    private static final int UNIT_SPACE = 1;

    public Spearman(Position position, int kingdomId) {
        super("Spearman", HEALTH, SPEED, ATTACK, RANGE, HP, MAX_HP, GOLD_COST, position, kingdomId, SPEED);
        this.foodCost = FOOD_COST;
        this.unitSpace = UNIT_SPACE;
    }

    @Override
    public boolean canMerge(Unit other) {
        return other instanceof Spearman && this.getKingdomId() == other.getKingdomId();
    }

    @Override
    public Unit merge(Unit other) {
        if (!canMerge(other)) throw new IllegalArgumentException("Cannot merge these units");
        return new Knight(this.getPosition(), this.getKingdomId());
    }

    @Override
    public boolean canMergeWith(Unit unit2) {
        return canMerge(unit2);
    }
}
