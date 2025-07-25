package org.Game.models.blocks;

import org.Game.models.Position;
import org.Game.models.structures.Structure;
import org.Game.models.units.Unit;

import java.io.Serializable;

public abstract class Block implements Serializable {
    protected Position position;
    protected boolean walkable;
    protected boolean absorbed;
    protected int kingdomId;

    private Structure structure;
    private Unit unit;
    private static final long serialVersionUID = -421376266421882466L;
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

    public boolean isForest() {
        return false;
    }
    public boolean isOccupied() {
        return unit != null;
    }
    public void removeStructure() {
        if (this.structure != null) {
            this.structure.setDestroyed(true);
            this.structure = null;
        }
    }


    public void setOwnerID(int kingdomId) {
        this.kingdomId = kingdomId;
    }

    public int getKingdomId() {
        return kingdomId;
    }

    public void setKingdomId(int kingdomId) {
        this.kingdomId = kingdomId;
        this.absorbed = kingdomId != -1;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block block = (Block) o;
        return position.equals(block.getPosition());
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

}
