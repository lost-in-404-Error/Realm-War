package org.Game.models.blocks;

import org.Game.models.Position;

import java.io.Serializable;

public class VoidBlock extends Block implements Serializable {
    public VoidBlock(Position position) {
        super(position, false);
    }

    @Override
    public boolean canBuildStructure() {
        return false;
    }

    @Override
    public int getResourceYield(String resourceType) {
        return 0;
    }

    @Override
    public int getGoldGeneration() {
        return 0;
    }

    @Override
    public int getFoodGeneration() {
        return 0;
    }

    @Override
    public String getBlockType() {
        return "Void";
    }
}

