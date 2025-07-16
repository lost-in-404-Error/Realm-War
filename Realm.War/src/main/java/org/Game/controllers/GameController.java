package org.Game.controllers;

import org.Game.models.*;
import org.Game.models.blocks.Block;
import org.Game.models.structures.*;

public class GameController {
    private final GameState gameState;
    private final StructureController structureController;

    public GameController(GameState gameState) {
        this.gameState = gameState;
        this.structureController = new StructureController(gameState);
    }

    public void startGame() {
        gameState.startGame();
    }

    public void stopGame() {
        gameState.stopGame();
    }

    public void nextTurn() {
        gameState.endTurn();
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean tryBuildStructure(String type, Position position) {
        Block[][] map = gameState.getGameMap();
        Kingdom currentKingdom = gameState.getCurrentKingdom();

        if (position == null || !isPositionValid(position, map)) {
            return false;
        }

        Block block = map[position.getX()][position.getY()];
        if (block == null || block instanceof org.Game.models.blocks.VoidBlock) {
            return false;
        }

        if (!currentKingdom.getAbsorbedBlocks().contains(block)) {
            return false;
        }

        Structure structure = switch (type.toLowerCase()) {
            case "farm" -> new Farm(position, block, currentKingdom.getId());
            case "barrack" -> new Barrack(position, block, currentKingdom.getId(),
                    currentKingdom.countStructuresOfType(Barrack.class));
            case "tower" -> new Tower(position, block, currentKingdom.getId());
            case "market" -> new Market(position, block, currentKingdom.getId(),
                    currentKingdom.countStructuresOfType(Market.class));
            default -> {
                yield null;
            }
        };

        if (structure == null) {
            return false;
        }

        int cost = structure.getBuildCostGold();
        if (currentKingdom.getGold() < cost) {

            return false;
        }

        boolean success = structureController.createStructure(structure);
        if (!success) {

        } else {
           
        }

        return success;
    }

    private boolean isPositionValid(Position pos, Block[][] map) {
        int x = pos.getX();
        int y = pos.getY();
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length;
    }


}
