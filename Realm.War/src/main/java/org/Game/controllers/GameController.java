package org.Game.controllers;

import org.Game.models.*;
import org.Game.models.blocks.Block;
import org.Game.models.blocks.VoidBlock;
import org.Game.models.structures.*;
import org.Game.models.units.*;

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
            default -> null;
        };

        if (structure == null) {
            return false;
        }

        int cost = structure.getBuildCostGold();
        if (currentKingdom.getGold() < cost) {
            return false;
        }

        boolean success = structureController.createStructure(structure);
        return success;
    }

    public boolean tryRecruitUnit(String unitType, Position position) {
        Block[][] map = gameState.getGameMap();
        Kingdom currentKingdom = gameState.getCurrentKingdom();

        if (position == null || !isPositionValid(position, map)) return false;

        Block block = map[position.getX()][position.getY()];
        if (block == null || !currentKingdom.getAbsorbedBlocks().contains(block)) return false;

        Unit unit;
        switch (unitType.toLowerCase()) {
            case "peasant" -> unit = new Peasant(position, currentKingdom.getId());
            case "spearman" -> unit = new Spearman(position, currentKingdom.getId());
            case "swordman" -> unit = new Swordman(position, currentKingdom.getId());
            case "knight" -> unit = new Knight(position, currentKingdom.getId());
            default -> {
                return false;
            }
        }

        if (currentKingdom.getGold() < unit.getGoldCost() || currentKingdom.getFood() < unit.getFoodCost()) {
            return false;
        }

        currentKingdom.getUnitController().addUnit(unit);
        block.setUnit(unit);

        currentKingdom.decreaseGold(unit.getGoldCost());
        currentKingdom.decreaseFood(unit.getFoodCost());

        return true;
    }

    private boolean isPositionValid(Position pos, Block[][] map) {
        int x = pos.getX();
        int y = pos.getY();
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length;
    }
    public boolean tryMoveUnit(Unit unit, Position destination) {
        if (unit == null || destination == null) return false;

        Position current = unit.getPosition();
        int distance = Math.abs(destination.getX() - current.getX()) + Math.abs(destination.getY() - current.getY());

        if (distance > unit.getMovementRange()) return false;

        Block destBlock = gameState.getBlockAt(destination);
        if (destBlock == null || destBlock instanceof org.Game.models.blocks.VoidBlock) return false;


        for (Unit other : gameState.getAllUnits()) {
            if (other.getPosition().equals(destination)) return false;
        }


        unit.setPosition(destination);
        return true;
    }
    public boolean tryAttackRange(Unit attacker, Unit target) {
        if (attacker == null || target == null) return false;

        if (attacker.getKingdomId() == target.getKingdomId()) return false;

        Position attackerPos = attacker.getPosition();
        Position targetPos = target.getPosition();

        int distance = Math.abs(attackerPos.getX() - targetPos.getX()) +
                Math.abs(attackerPos.getY() - targetPos.getY());

        if (distance > attacker.getAttackRange()) return false;


        target.takeDamage(attacker.getAttackPower());


        if (target.getHitPoints() <= 0) {
            Kingdom enemyKingdom = gameState.getKingdomById(target.getKingdomId());
            enemyKingdom.getUnits().remove(target);
        }

        return true;
    }
    public boolean tryMovementUnit(Unit unit, Position destination) {
        if (unit == null || destination == null) return false;

        Block[][] map = gameState.getGameMap();

        if (!isPositionValid(destination, map)) return false;

        Block destBlock = map[destination.getX()][destination.getY()];
        if (destBlock instanceof VoidBlock) return false;


        int dx = Math.abs(unit.getPosition().getX() - destination.getX());
        int dy = Math.abs(unit.getPosition().getY() - destination.getY());

        if (dx + dy > unit.getMovementRange()) return false;

        unit.setPosition(destination);
        return true;
    }

}
