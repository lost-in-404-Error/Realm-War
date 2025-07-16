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
    private int movementRange;
    private int hitPoints;
    private int maxHitPoints;

    public Unit(String name, int health, int movement, int attackPower, int attackRange,
                int hitPoints, int maxHitPoints, int goldCost, Position position,
                int kingdomId, int movementRange) {

        this.name = name;
        this.health = health;
        this.movement = movement;
        this.attackPower = attackPower;
        this.attackRange = attackRange;
        this.hitPoints = hitPoints;
        this.maxHitPoints = maxHitPoints;
        this.goldCost = goldCost;
        this.position = position;
        this.kingdomId = kingdomId;
        this.movementRange = movementRange;


        this.foodCost = 0;
        this.unitSpace = 0;
        this.paymentCost = 0;
        this.rationCost = 0;
    }

    // --- Getters & Setters ---

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
        this.kingdomId = kingdomId;

    }

    public int getMovementRange() {
        return movementRange;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = Math.max(0, Math.min(hitPoints, maxHitPoints));
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public int getPaymentCost() {
        return paymentCost;
    }

    public int getRationCost() {
        return rationCost;
    }

    public String getType() {
        return "Unit";
    }



    public void takeDamage(int damage) {
        this.hitPoints = Math.max(0, this.hitPoints - damage);
    }



    public abstract boolean canMerge(Unit other);

    public abstract Unit merge(Unit other);

    public abstract boolean canMergeWith(Unit unit2);
}
