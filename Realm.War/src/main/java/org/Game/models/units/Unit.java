package org.Game.models.units;

import org.Game.models.Position;

import java.io.Serializable;

public abstract class Unit  implements Serializable {
    protected String name;
    protected int hitPoints;
    protected int maxHitPoints;
    protected int movementRange;
    protected int attackPower;
    protected int attackRange;
    protected int paymentCost;
    protected int rationCost;
    protected int unitSpace;
    protected int defense;
    protected Position position;
    protected int kingdomId;


    public Unit(int hitPoints, int movementRange, int attackPower, int attackRange,
                int paymentCost, int rationCost, int unitSpace,
                Position position, int kingdomId) {

        this.name = getClass().getSimpleName();
        this.hitPoints = hitPoints;
        this.maxHitPoints = hitPoints;
        this.movementRange = movementRange;
        this.attackPower = attackPower;
        this.attackRange = attackRange;
        this.paymentCost = paymentCost;
        this.rationCost = rationCost;
        this.unitSpace = unitSpace;
        this.defense = defense;
        this.position = position;
        this.kingdomId = kingdomId;
    }

    // ------------------ Getters --------------------

    public String getName() { return name; }
    public int getHitPoints() { return hitPoints; }
    public int getMaxHitPoints() { return maxHitPoints; }
    public int getMovementRange() { return movementRange; }
    public int getAttackPower() { return attackPower; }
    public int getAttackRange() { return attackRange; }
    public int getPaymentCost() { return paymentCost; }
    public int getRationCost() { return rationCost; }
    public int getUnitSpace() { return unitSpace; }
    public int getDefense() { return defense; }
    public Position getPosition() { return position; }
    public int getKingdomId() { return kingdomId; }


    public void setPosition(Position position) { this.position = position; }
    public void setKingdomId(int kingdomId) { this.kingdomId = kingdomId; }
    public void setHitPoints(int hp) {
        this.hitPoints = Math.max(0, Math.min(hp, maxHitPoints));
    }

    // ------------------ Core Logic --------------------

    public boolean isDead() {
        return hitPoints <= 0;
    }

    public void takeDamage(int damage) {
        this.hitPoints = Math.max(0, this.hitPoints - damage);
    }

    public boolean isInRange(Unit target) {
        if (target == null) return false;
        int dx = Math.abs(this.position.getX() - target.getPosition().getX());
        int dy = Math.abs(this.position.getY() - target.getPosition().getY());
        return dx + dy <= this.attackRange;
    }

    public void attack(Unit target) {
        if (target == null) throw new IllegalArgumentException("Target is null.");
        if (!isInRange(target)) throw new IllegalArgumentException("Target out of attack range.");
        if (this.kingdomId == target.getKingdomId()) throw new IllegalArgumentException("Cannot attack your own unit.");

        target.takeDamage(this.attackPower);
    }

    public abstract boolean canMerge(Unit other);

    public boolean canMergeWith(Unit other) {
        return other != null
                && this.getClass().equals(other.getClass())
                && this.getKingdomId() == other.getKingdomId()
                && this.getPosition().isAdjacentTo(other.getPosition());
    }


    public abstract Unit merge(Unit other);

    public int getGoldCost() {
        return this.hitPoints * this.movementRange;
    }

    public int getFoodCost() {
        return this.hitPoints * this.movementRange;
    }

    public boolean canMoveTo(Position dest) {
        if (dest == null) return false;

        int dx = Math.abs(this.position.getX() - dest.getX());
        int dy = Math.abs(this.position.getY() - dest.getY());
        return dx + dy <= this.movementRange;
    }

    public void moveTo(Position dest) {
        if (!canMoveTo(dest)) {
            throw new IllegalArgumentException("Destination out of movement range.");
        }

        this.position = dest;
    }

    public static Unit upgradeUnit(Unit oldUnit) {
        Position pos = oldUnit.getPosition();
        int kingdomId = oldUnit.getKingdomId();

        if (oldUnit instanceof Peasant) {
            return new Spearman(pos, kingdomId);
        } else if (oldUnit instanceof Spearman) {
            return new Swordman(pos, kingdomId);
        } else if (oldUnit instanceof Swordman) {
            return new Knight(pos, kingdomId);
        }

        return oldUnit;
    }
    public abstract Unit mergeWith(Unit u2);

}
