package org.Game.models.blocks;

import org.Game.models.Position;

public class EmptyBlock extends Block {
    private static final int GOLD_GENERATION = 10;
    private static final int FOOD_GENERATION = 0;

    public EmptyBlock(Position position) {
        super(position, true);
    }

    @Override
    public boolean canBuildStructure() {
        return absorbed;
    }

    @Override
    public int getResourceYield(String resourceType) {
        switch (resourceType.toLowerCase()) {
            case "gold": return absorbed ? GOLD_GENERATION : 0;
            case "food": return FOOD_GENERATION;
            default: return 0;
        }
    }

    @Override
    public int getGoldGeneration() {
        return absorbed ? GOLD_GENERATION : 0;
    }

    @Override
    public int getFoodGeneration() {
        return FOOD_GENERATION;
    }

    @Override
    public String getBlockType() {
        return "Empty";
    }
}
