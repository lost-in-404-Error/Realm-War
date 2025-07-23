package org.Game.models.blocks;

import org.Game.models.Position;

import java.io.Serializable;

public class ForestBlock extends Block implements Serializable {
    public ForestBlock(Position position) {
        super(position, true);
    }

    @Override
    public boolean canBuildStructure() {
        return absorbed;
    }

    @Override
    public int getResourceYield(String resourceType) {
        if (!absorbed) return 0;
        return switch (resourceType.toLowerCase()) {
            case "food" -> 2;
            case "gold" -> 0;
            default -> 0;
        };
    }

    @Override
    public int getGoldGeneration() {
        return 0;
    }

    @Override
    public int getFoodGeneration() {
        return absorbed ? 2 : 0;
    }

    @Override
    public String getBlockType() {
        return "Forest";
    }

    @Override
    public boolean isForest() {
        return true;
    }
}
