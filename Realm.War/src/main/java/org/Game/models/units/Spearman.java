package org.Game.models.units;

import org.Game.models.Position;

import java.io.Serializable;

public class Spearman extends Unit implements Serializable {

    public Spearman(Position position, int kingdomId) {
        super(50, 2, 10, 1, 4, 2, 2, position, kingdomId);
    }

    @Override
    public boolean canMerge(Unit other) {
        return other instanceof Spearman &&
                this.getKingdomId() == other.getKingdomId();
    }

    @Override
    public Unit merge(Unit other) {
        if (!canMerge(other)) {
            throw new IllegalArgumentException("Cannot merge these units");
        }
        return new Swordman(this.getPosition(), this.getKingdomId());
    }

    @Override
    public boolean canMergeWith(Unit unit2) {
        return canMerge(unit2);
    }
    @Override
    public Unit mergeWith(Unit other) {
        return new Swordman(this.position, this.kingdomId);
    }
}
