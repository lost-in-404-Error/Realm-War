package org.Game.controllers;

import org.Game.models.Position;
import org.Game.models.units.Unit;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class UnitController {
    private final List<Unit> units;

    public UnitController(List<Unit> units) {
        this.units = (units != null) ? new ArrayList<>(units) : new ArrayList<>();
    }

    public void addUnit(Unit unit) {
        if (unit != null) {
            units.add(unit);
        }
    }

    public boolean move(Unit unit, int x, int y) {
        if (unit == null) return false;

        unit.setPosition(new Position(x, y));
        return true;
    }

    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public boolean merge(Unit unit1, Unit unit2) {
        if (unit1 == null || unit2 == null || !unit1.canMergeWith(unit2)) {
            return false;
        }

        Unit mergedUnit = unit1.merge(unit2);
        units.remove(unit1);
        units.remove(unit2);
        units.add(mergedUnit);
        return true;
    }

    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }
}
