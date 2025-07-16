package org.Game.models.blocks;

import org.Game.models.Position;
import org.Game.models.structures.Structure;
import org.Game.models.units.Unit;

public abstract class Block {
    protected Position position;
    protected boolean walkable;
    protected boolean absorbed;
    protected int kingdomId; // -1 means no owner

    private Structure structure;
    private Unit unit;

    public Block(Position position, boolean walkable) {
        this.position = position;
        this.walkable = walkable;
        this.absorbed = false;
        this.kingdomId = -1;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isAbsorbed() {
        return absorbed;
    }

    public void setAbsorbed(boolean absorbed, int kingdomId) {
        this.absorbed = absorbed;
        if (absorbed) {
            this.kingdomId = kingdomId;
        } else {
            this.kingdomId = -1;
        }


    }

    public int getOwnerID() {
        return kingdomId;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public abstract boolean canBuildStructure();
    public abstract int getResourceYield(String resourceType);
    public abstract int getGoldGeneration();
    public abstract int getFoodGeneration();
    public abstract String getBlockType();

    public boolean hasStructure() {
        return structure != null;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public boolean hasUnit() {
        return unit != null;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isBuildable() {
        return walkable;
    }
}
