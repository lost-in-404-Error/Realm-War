package org.Game.models.units;

import org.Game.models.Position;

public class Swordman extends Unit {
    private static final int HEALTH = 60;
    private static final int SPEED = 2;
    private static final int ATTACK = 10;
    private static final int RANGE = 1;
    private static final int HP = 80;
    private static final int MAX_HP = 80;
    private static final int GOLD_COST = 4;
    private static final int FOOD_COST = 2;
    private static final int UNIT_SPACE = 2;

    public Swordman(Position position, int kingdomId) {
        super("Swordman", HEALTH, SPEED, ATTACK, RANGE, HP, MAX_HP, GOLD_COST, position, kingdomId, SPEED);
        this.foodCost = FOOD_COST;
        this.unitSpace = UNIT_SPACE;
    }

    @Override
    public boolean canMerge(Unit other) {
        return other instanceof Swordman && this.getKingdomId() == other.getKingdomId();
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
