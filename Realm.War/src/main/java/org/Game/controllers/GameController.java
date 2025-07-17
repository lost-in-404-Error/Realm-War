package org.Game.controllers;

import org.Game.models.*;
import org.Game.models.blocks.Block;
import org.Game.models.blocks.VoidBlock;
import org.Game.models.structures.*;
import org.Game.models.units.*;

import java.util.HashMap;
import java.util.List;

public class GameController {
    private final GameState gameState;
    private final StructureController structureController;
    private Unit selectedUnit;
    private Unit unitController;

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

        if (position == null || !isPositionValid(position, map)) return false;

        Block block = map[position.getX()][position.getY()];
        if (block == null || block instanceof VoidBlock) return false;
        if (!currentKingdom.getAbsorbedBlocks().contains(block)) return false;

        Structure structure = switch (type.toLowerCase()) {
            case "farm" -> new Farm(position, block, currentKingdom.getId());
            case "barrack" -> new Barrack(position, block, currentKingdom.getId(),
                    currentKingdom.countStructuresOfType(Barrack.class));
            case "tower" -> new Tower(position, block, currentKingdom.getId());
            case "market" -> new Market(position, block, currentKingdom.getId(),
                    currentKingdom.countStructuresOfType(Market.class));
            default -> null;
        };

        if (structure == null || currentKingdom.getGold() < structure.getBuildCostGold()) return false;

        boolean success = structureController.createStructure(structure);
        if (success) {
            currentKingdom.decreaseGold(structure.getBuildCostGold());
        }
        return success;
    }

    public boolean tryRecruitUnit(String unitType, Position position) {
        Block[][] map = gameState.getGameMap();
        Kingdom currentKingdom = gameState.getCurrentKingdom();

        if (position == null || !isPositionValid(position, map)) return false;

        Block block = map[position.getX()][position.getY()];
        if (block == null || !currentKingdom.getAbsorbedBlocks().contains(block)) return false;
        if (block.getUnit() != null) return false;

        Unit unit = switch (unitType.toLowerCase()) {
            case "peasant" -> new Peasant(position, currentKingdom.getId());
            case "spearman" -> new Spearman(position, currentKingdom.getId());
            case "swordman" -> new Swordman(position, currentKingdom.getId());
            case "knight" -> new Knight(position, currentKingdom.getId());
            default -> null;
        };

        if (unit == null || currentKingdom.getGold() < unit.getGoldCost() || currentKingdom.getFood() < unit.getFoodCost())
            return false;

        currentKingdom.getUnitController().addUnit(unit);
        block.setUnit(unit);
        currentKingdom.decreaseGold(unit.getGoldCost());
        currentKingdom.decreaseFood(unit.getFoodCost());

        return true;
    }

    public boolean tryMoveUnit(Unit unit, Position destination) {
        if (unit == null || destination == null) return false;
        if (!isPositionValid(destination, gameState.getGameMap())) return false;

        Block destBlock = gameState.getBlockAt(destination);
        if (destBlock == null || destBlock instanceof VoidBlock || destBlock.getUnit() != null) return false;

        int dx = Math.abs(unit.getPosition().getX() - destination.getX());
        int dy = Math.abs(unit.getPosition().getY() - destination.getY());
        if (dx + dy > unit.getMovementRange()) return false;

        Block sourceBlock = gameState.getBlockAt(unit.getPosition());
        if (sourceBlock != null) sourceBlock.setUnit(null);

        unit.setPosition(destination);
        destBlock.setUnit(unit);

        Kingdom owner = gameState.getKingdomById(unit.getKingdomId());
        if (owner != null) owner.absorbBlock(destBlock);

        return true;
    }

    public boolean tryAttack(Position attackerPos, Position targetPos) {
        if (attackerPos == null || targetPos == null) return false;

        Unit attacker = gameState.getUnitAt(attackerPos);
        Unit target = gameState.getUnitAt(targetPos);

        if (attacker == null || target == null) return false;
        if (attacker.getKingdomId() == target.getKingdomId()) return false;

        int distance = Math.abs(attackerPos.getX() - targetPos.getX()) +
                Math.abs(attackerPos.getY() - targetPos.getY());
        if (distance > attacker.getAttackRange()) return false;

        Block attackerBlock = gameState.getBlockAt(attackerPos);
        Block targetBlock = gameState.getBlockAt(targetPos);
        int bonus = 0;
        if (attackerBlock != null && attackerBlock.isForest()) bonus += 2;
        if (targetBlock != null && targetBlock.isForest()) bonus -= 2;

        int finalDamage = Math.max(0, attacker.getAttackPower() + bonus);
        target.takeDamage(finalDamage);

        if (target.getHitPoints() <= 0) {
            Kingdom enemy = gameState.getKingdomById(target.getKingdomId());
            if (enemy != null) enemy.getUnits().remove(target);
            if (targetBlock != null) targetBlock.setUnit(null);
        }

        return true;
    }


    public boolean hasFriendlyUnitAt(Position pos) {
        Kingdom current = gameState.getCurrentKingdom();
        for (Unit unit : current.getUnits()) {
            if (unit.getPosition().equals(pos)) return true;
        }
        return false;
    }

    private boolean isPositionValid(Position pos, Block[][] map) {
        int x = pos.getX(), y = pos.getY();
        return x >= 0 && y >= 0 && x < map.length && y < map[0].length;
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public void startMoveMode() {

    }



    public boolean tryMerge(Unit u1, Unit u2) {
        if (u1.canMergeWith(u2)) {

            Kingdom kingdom = gameState.getKingdomById(u1.getKingdomId());
            kingdom.getUnits().remove(u1);
            kingdom.getUnits().remove(u2);


            Unit mergedUnit = u1.mergeWith(u2);


            kingdom.getUnits().add(mergedUnit);


            gameState.getGameMap()[mergedUnit.getPosition().getX()][mergedUnit.getPosition().getY()].setUnit(mergedUnit);

            return true;
        }
        return false;
    }


}
