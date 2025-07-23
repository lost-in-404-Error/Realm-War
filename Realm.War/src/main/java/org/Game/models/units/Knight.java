package org.Game.models.units;

import org.Game.models.Position;

import java.io.Serializable;

public class Knight extends Unit implements Serializable {
    public Knight(Position position, int kingdomId) {
        super(120, 3, 25, 1, 12, 4, 6, position, kingdomId);
    }

    @Override
    public boolean canMerge(Unit other) {
        return false;
    }

    @Override
    public Unit merge(Unit other) {
        throw new UnsupportedOperationException("Knight cannot be merged.");
    }

    @Override
    public Unit mergeWith(Unit u2) {
        return null;
    }

    @Override
    public boolean canMergeWith(Unit other) {
        return false;
    }
}
