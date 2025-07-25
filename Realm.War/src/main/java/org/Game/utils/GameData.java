package org.Game.utils;

import org.Game.models.Kingdom;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class GameData {
    private List<Kingdom> kingdoms;
    private List<Kingdom> winners;

    public List<Kingdom> getKingdoms() {
        return kingdoms;
    }

    public void setKingdoms(List<Kingdom> kingdoms) {
        this.kingdoms = kingdoms;
    }

    public List<Kingdom> getWinners() {
        return winners;
    }

    public void setWinners(List<Kingdom> winners) {
        this.winners = winners;
    }

}

