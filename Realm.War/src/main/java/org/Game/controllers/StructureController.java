package org.Game.controllers;

import org.Game.models.Position;
import org.Game.models.structures.Structure;

import java.util.Iterator;
import java.util.List;

public class StructureController {
    private final List<Structure> structures;

    public StructureController(List<Structure> structures) {
        this.structures = structures;
    }


    public boolean createStructure(Structure structure) {
        if (findStructureAt(structure.getPosition()) != null) {
            return false;
        }
        structures.add(structure);
        return true;
    }


    public boolean upgradeStructure(Structure structure) {
        if (structure == null || !structure.canUpgrade()) {
            return false;
        }
        structure.upgrade();
        return true;
    }


    public boolean removeStructure(Position position) {
        Iterator<Structure> iterator = structures.iterator();
        while (iterator.hasNext()) {
            Structure s = iterator.next();
            if (s.getPosition().equals(position)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }


    public Structure findStructureAt(Position position) {
        for (Structure s : structures) {
            if (s.getPosition().equals(position)) {
                return s;
            }
        }
        return null;
    }


    public List<Structure> getStructures() {
        return structures;
    }
}
