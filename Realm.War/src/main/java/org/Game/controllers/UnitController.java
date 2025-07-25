package org.Game.controllers;

import org.Game.models.GameState;
import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.units.Unit;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class UnitController {
    private final List<Unit> units;
    private GameState gameState;

    public UnitController(List<Unit> units) {
        this.units = (units != null) ? new ArrayList<>(units) : new ArrayList<>();
    }

    public void addUnit(Unit unit) {
        if (unit != null) {
            units.add(unit);
        }
    }


    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    public boolean merge(Unit unit1, Unit unit2) {
        if (unit1 == null || unit2 == null || !unit1.canMergeWith(unit2)) return false;

        Unit merged = unit1.merge(unit2);

        gameState.getCurrentKingdom().getUnits().remove(unit1);
        gameState.getCurrentKingdom().getUnits().remove(unit2);
        gameState.getCurrentKingdom().getUnits().add(merged);
        return true;
    }


    public boolean move(Unit unit, int x, int y, Block[][] gameMap) {
        if (unit == null || gameMap == null) return false;

        int width = gameMap.length;
        int height = gameMap[0].length;

        if (x < 0 || x >= width || y < 0 || y >= height) return false;

        Position destination = new Position(x, y);
        int distance = Math.abs(unit.getPosition().getX() - x) + Math.abs(unit.getPosition().getY() - y);

        if (distance > unit.getMovementRange()) return false;

        Block destBlock = gameMap[x][y];


        if (destBlock == null || destBlock instanceof org.Game.models.blocks.VoidBlock) return false;
        if (destBlock.getUnit() != null) return false;

        Position prev = unit.getPosition();
        if (prev != null) {
            gameMap[prev.getX()][prev.getY()].setUnit(null);
        }


        unit.moveTo(destination);
        destBlock.setUnit(unit);

        return true;
    }


    public void attack(Block attackerBlock, Block targetBlock) {
        if (attackerBlock == null || targetBlock == null) {
            JOptionPane.showMessageDialog(null, "Invalid block selected.");
            return;
        }

        Unit attacker = attackerBlock.getUnit();
        Unit target = targetBlock.getUnit();

        if (attacker == null || target == null) {
            JOptionPane.showMessageDialog(null, "No unit found at selected blocks.");
            return;
        }

        if (attacker.getKingdomId() == target.getKingdomId()) {
            JOptionPane.showMessageDialog(null, "Cannot attack your own unit.");
            return;
        }

        int distance = Math.abs(attacker.getPosition().getX() - target.getPosition().getX())
                + Math.abs(attacker.getPosition().getY() - target.getPosition().getY());

        if (distance > attacker.getAttackRange()) {
            JOptionPane.showMessageDialog(null, "Target is out of range.");
            return;
        }

        int bonus = 0;
        if (attackerBlock.isForest()) bonus += 2;
        if (targetBlock.isForest()) bonus -= 2;

        int finalDamage = Math.max(0, attacker.getAttackPower() + bonus);
        target.takeDamage(finalDamage);

        if (target.getHitPoints() <= 0) {
            targetBlock.setUnit(null);
            removeUnit(target);
            JOptionPane.showMessageDialog(null, "Target destroyed!");
        } else {
            JOptionPane.showMessageDialog(null, "Attack successful! Target HP: " + target.getHitPoints());
        }
    }


    public List<Unit> getUnits() {
        return Collections.unmodifiableList(units);
    }
}
