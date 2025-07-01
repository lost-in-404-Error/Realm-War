package org.Game.controllers;


import org.Game.models.structures.Structure;

import java.util.List;

import java.util.ArrayList;
import java.util.Collections;

public class StructureController {
    private final List<Structure> structures;

    public StructureController(List<Structure> structures) {
        if (structures == null) {
            this.structures = new ArrayList<>();
        } else {
            this.structures = structures;
        }
    }

    public void addStructure(Structure structure) {
        if (structure != null) {
            structures.add(structure);
        }
    }

    public boolean levelUpStructure(Structure structure) {
        if (structure != null && structure.canLevelUp()) {
            structure.levelUp();
            return true;
        }
        return false;
    }

    public void removeStructure(Structure structure) {
        if (structure != null) {
            structures.remove(structure);
        }
    }

    public List<Structure> getStructures() {
        return Collections.unmodifiableList(structures);
    }
}
