package org.Game.models.units;

import org.Game.models.Position;

import java.io.Serializable;

public class Swordman extends Unit implements Serializable {

    public Swordman(Position position, int kingdomId) {
        super(80, 4, 15, 1, 8, 4, 3, position, kingdomId);
    }

    @Override
    public boolean canMerge(Unit other) {
        return other instanceof Swordman &&
                this.getKingdomId() == other.getKingdomId();
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
    @Override
    public Unit mergeWith(Unit other) {
        return new Knight(this.position, this.kingdomId);
    }
}
