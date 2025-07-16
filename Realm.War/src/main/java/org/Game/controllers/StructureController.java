package org.Game.controllers;

import org.Game.models.GameState;
import org.Game.models.Position;
import org.Game.models.blocks.Block;
import org.Game.models.structures.Structure;
import org.Game.models.structures.Farm;
import org.Game.models.Kingdom;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class StructureController {
    private List<Structure> structures;
    private final GameState gameState;

    public StructureController(GameState gameState) {
        this.structures = new ArrayList<>();
        this.gameState = gameState;
    }


    public boolean createStructure(Structure structure) {
        Position pos = structure.getPosition();
        Block block = structure.getBaseBlock();
        Kingdom kingdom = gameState.getKingdom(structure.getKingdomId());

        if (findStructureAt(pos) != null || !block.isBuildable() || block.hasStructure()) {
            return false;
        }

        int goldCost = structure.getBuildCostGold();
        int foodCost = structure.getBuildCostFood();

        if (kingdom.getGold() < goldCost || kingdom.getFood() < foodCost) {
            return false;
        }


        kingdom.subtractGold(goldCost);
        kingdom.subtractFood(foodCost);


        structures.add(structure);
        block.setStructure(structure);
        kingdom.addStructure(structure);

        return true;
    }

    public boolean upgradeStructure(Structure structure) {
        Kingdom kingdom = gameState.getKingdom(structure.getKingdomId());

        if (structure == null || !structure.canUpgrade()) {
            return false;
        }

        int upgradeCost = structure instanceof Farm ?
                ((Farm) structure).getUpgradeCost() : 10; // fallback cost

        if (kingdom.getGold() < upgradeCost) {
            return false;
        }


        kingdom.subtractGold(upgradeCost);
        structure.upgrade();

        return true;
    }


    public boolean removeStructure(Position position) {
        Iterator<Structure> iterator = structures.iterator();
        while (iterator.hasNext()) {
            Structure s = iterator.next();
            if (s.getPosition().equals(position)) {
                Block block = s.getBaseBlock();
                Kingdom kingdom = gameState.getKingdom(s.getKingdomId());

                block.setStructure(null);
                kingdom.removeStructure(s);
                iterator.remove();
                return true;
            }
        }
        return false;
    }


    public Structure findStructureAt(Position position) {
        for (Structure s : structures) {
            if (s.getPosition().equals(position)) {
                return s;
            }
        }
        return null;
    }


    public List<Structure> getStructures() {
        return new ArrayList<>(structures);
    }


    public void performTurnActions() {
        for (Structure s : structures) {
            Kingdom kingdom = gameState.getKingdom(s.getKingdomId());
            s.performTurnAction(kingdom, gameState);
        }
    }


    public List<Structure> getStructuresByKingdom(int kingdomId) {
        return structures.stream()
                .filter(s -> s.getKingdomId() == kingdomId)
                .collect(Collectors.toList());
    }


}
