package org.Game.models.units;

import org.Game.models.Position;

public abstract class Unit {

    protected String name;
    protected int health;
    protected int movement;
    protected int attackPower;
    protected int attackRange;
    protected int goldCost;
    protected int foodCost;
    protected int unitSpace;
    protected Position position;
    protected int kingdomId;
    private int paymentCost;
    private int rationCost;
    public Unit(String name, int health, int movement, int attackPower, int attackRange,
                int goldCost, int foodCost, int unitSpace, Position position, int kingdomId) {
        this.name = name;
        this.health = health;
        this.movement = movement;
        this.attackPower = attackPower;
        this.attackRange = attackRange;
        this.goldCost = goldCost;
        this.foodCost = foodCost;
        this.unitSpace = unitSpace;
        this.position = position;
        this.kingdomId = kingdomId;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
    }

    public int getMovement() {
        return movement;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getGoldCost() {
        return goldCost;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public int getUnitSpace() {
        return unitSpace;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getKingdomId() {
        return kingdomId;
    }

    public void setKingdomId(int id) {
        this.kingdomId = id;
    }

    public abstract boolean canMerge(Unit other);

    public abstract Unit merge(Unit other);
    public abstract boolean canMergeWith(Unit unit2);

    public int getPaymentCost() { return paymentCost; }
    public int getRationCost() { return rationCost; }
}
