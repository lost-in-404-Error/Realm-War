package org.Game.utils;

import org.Game.models.Kingdom;
import org.Game.models.Player;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/realm_war_game";
    private static final String USER = "postgres";
    private static final String PASS = "2005";

    public void createTables() {
        String createGamesTable = "CREATE TABLE IF NOT EXISTS public.games(" +
                "id SERIAL PRIMARY KEY, " +
                "played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String createWinnersTable = "CREATE TABLE IF NOT EXISTS winners (" +
                "id SERIAL PRIMARY KEY, " +
                "game_id INTEGER REFERENCES games(id) ON DELETE CASCADE, " +
                "name TEXT NOT NULL, " +
                "food INTEGER NOT NULL, " +
                "gold INTEGER NOT NULL" +
                ");";

        String createKingdomsTable = "CREATE TABLE IF NOT EXISTS kingdoms (" +
                "id SERIAL PRIMARY KEY, " +
                "game_id INTEGER REFERENCES games(id) ON DELETE CASCADE, " +
                "kingdom_id INTEGER NOT NULL, " +
                "gold INTEGER NOT NULL, " +
                "food INTEGER NOT NULL, " +
                "owned_blocks TEXT" +
                ");";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createGamesTable);
            stmt.execute(createWinnersTable);
            stmt.execute(createKingdomsTable);
            GameLogger.log("Tables 'games', 'winners', and 'kingdoms' created successfully.");

        } catch (SQLException e) {
            GameLogger.logError("Failed to create tables", e);
        }
    }

    public int saveGameData(ArrayList<Player> winners, List<Kingdom> kingdoms) {
        String insertGameSQL = "INSERT INTO games DEFAULT VALUES RETURNING id;";
        String insertWinnerSQL = "INSERT INTO winners(game_id, name, food, gold) VALUES (?, ?, ?, ?);";
        String insertKingdomSQL = "INSERT INTO kingdoms(game_id, kingdom_id, gold, food, owned_blocks) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement insertGameStmt = conn.prepareStatement(insertGameSQL);
             PreparedStatement insertWinnerStmt = conn.prepareStatement(insertWinnerSQL);
             PreparedStatement insertKingdomStmt = conn.prepareStatement(insertKingdomSQL)) {

            conn.setAutoCommit(false);

            ResultSet rs = insertGameStmt.executeQuery();
            int gameId = -1;
            if (rs.next()) {
                gameId = rs.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("Failed to insert new game.");
            }

            for (int i = 0; i < winners.size(); i++) {
                Player p = winners.get(i);
                Kingdom k = kingdoms.get(i);

                insertWinnerStmt.setInt(1, gameId);
                insertWinnerStmt.setString(2, p.getName());
                insertWinnerStmt.setInt(3, k.getFood());
                insertWinnerStmt.setInt(4, k.getGold());
                insertWinnerStmt.executeUpdate();
            }

            for (Kingdom k : kingdoms) {
                insertKingdomStmt.setInt(1, gameId);
                insertKingdomStmt.setInt(2, k.getId());
                insertKingdomStmt.setInt(3, k.getGold());
                insertKingdomStmt.setInt(4, k.getFood());
                insertKingdomStmt.setString(5, k.getOwnedBlocks());
                insertKingdomStmt.executeUpdate();
            }

            conn.commit();
            GameLogger.log("Game saved successfully with " + winners.size() + " winners and " + kingdoms.size() + " kingdoms.");
            return gameId;

        } catch (SQLException e) {
            GameLogger.logError("Failed to save game data", e);
            return -1;
        }
    }

    public void dropTables() {
        String sqlKingdoms = "DROP TABLE IF EXISTS kingdoms CASCADE;";
        String sqlWinners = "DROP TABLE IF EXISTS winners CASCADE;";
        String sqlGames = "DROP TABLE IF EXISTS games CASCADE;";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sqlKingdoms);
            stmt.executeUpdate(sqlWinners);
            stmt.executeUpdate(sqlGames);
            GameLogger.log("Tables 'games', 'winners', and 'kingdoms' dropped successfully.");

        } catch (SQLException e) {
            GameLogger.logError("Error dropping tables", e);
        }
    }


}
