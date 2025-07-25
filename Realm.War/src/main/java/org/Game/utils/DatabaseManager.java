package org.Game.utils;

import org.Game.models.Kingdom;
import org.Game.models.Player;

import java.sql.*;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:postgresql://localhost:5432/realm_war_game";
    private static final String USER = "postgres";
    private static final String PASS = "2005";

    public void createTables() {
        String createGamesTable = "CREATE TABLE IF NOT EXISTS games (" +
                "id SERIAL PRIMARY KEY, " +
                "played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        String createWinnersTable = "CREATE TABLE IF NOT EXISTS winners (" +
                "id SERIAL PRIMARY KEY, " +
                "game_id INTEGER REFERENCES games(id) ON DELETE CASCADE, " +
                "player_name TEXT NOT NULL, " +
                "kingdom_id INTEGER NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "food INTEGER NOT NULL, " +
                "gold INTEGER NOT NULL" +
                ");";

        String createKingdomsTable = "CREATE TABLE IF NOT EXISTS kingdoms (" +
                "id SERIAL PRIMARY KEY, " +
                "game_id INTEGER REFERENCES games(id) ON DELETE CASCADE, " +
                "kingdom_id INTEGER NOT NULL, " +
                "player_name TEXT NOT NULL, " +
                "food INTEGER NOT NULL, " +
                "gold INTEGER NOT NULL, " +
                "owned_blocks TEXT" +
                ");";

        String createPlayersResultTable = "CREATE TABLE IF NOT EXISTS players_result (" +
                "id SERIAL PRIMARY KEY, " +
                "game_id INTEGER REFERENCES games(id) ON DELETE CASCADE, " +
                "player_name TEXT NOT NULL, " +
                "kingdom_id INTEGER NOT NULL, " +
                "score INTEGER NOT NULL, " +
                "food INTEGER NOT NULL, " +
                "gold INTEGER NOT NULL, " +
                "is_winner BOOLEAN NOT NULL" +
                ");";


        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createGamesTable);
            stmt.execute(createWinnersTable);
            stmt.execute(createKingdomsTable);
            stmt.execute(createPlayersResultTable);
            GameLogger.log("✅ Tables created successfully.");

        } catch (SQLException e) {
            GameLogger.logError("❌ Failed to create tables.", e);
        }
    }

    public int saveGameData(List<Kingdom> winners, List<Kingdom> kingdoms) {
        String insertGameSQL = "INSERT INTO games DEFAULT VALUES RETURNING id;";
        String insertWinnerSQL = "INSERT INTO winners(game_id, player_name, kingdom_id, score, food, gold) VALUES (?, ?, ?, ?, ?, ?);";
        String insertKingdomSQL = "INSERT INTO kingdoms(game_id, kingdom_id, player_name, food, gold, owned_blocks) VALUES (?, ?, ?, ?, ?, ?);";
        String insertResultSQL = "INSERT INTO players_result(game_id, player_name, kingdom_id, score, food, gold, is_winner) VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement gameStmt = conn.prepareStatement(insertGameSQL);
             PreparedStatement winnerStmt = conn.prepareStatement(insertWinnerSQL);
             PreparedStatement kingdomStmt = conn.prepareStatement(insertKingdomSQL);
             PreparedStatement resultStmt = conn.prepareStatement(insertResultSQL)) {

            conn.setAutoCommit(false);

            ResultSet rs = gameStmt.executeQuery();
            int gameId = -1;
            if (rs.next()) {
                gameId = rs.getInt(1);
            } else {
                conn.rollback();
                GameLogger.log("❌ Failed to insert game (no ID returned).");
                return -1;
            }

            for (Kingdom winner : winners) {
                Player player = winner.getPlayer();
                if (player != null) {
                    winnerStmt.setInt(1, gameId);
                    winnerStmt.setString(2, player.getName());
                    winnerStmt.setInt(3, winner.getId());
                    winnerStmt.setInt(4, player.getScore());
                    winnerStmt.setInt(5, winner.getFood());
                    winnerStmt.setInt(6, winner.getGold());
                    winnerStmt.executeUpdate();
                    GameLogger.log("✅ Winner saved: " + player.getName());
                }
            }

            for (Kingdom k : kingdoms) {
                Player player = k.getPlayer();
                if (player != null) {

                    kingdomStmt.setInt(1, gameId);
                    kingdomStmt.setInt(2, k.getId());
                    kingdomStmt.setString(3, player.getName());
                    kingdomStmt.setInt(4, k.getFood());
                    kingdomStmt.setInt(5, k.getGold());
                    kingdomStmt.setString(6, k.getOwnedBlocks());
                    kingdomStmt.executeUpdate();
                    GameLogger.log("✅ Kingdom saved: " + player.getName());


                    resultStmt.setInt(1, gameId);
                    resultStmt.setString(2, player.getName());
                    resultStmt.setInt(3, k.getId());
                    resultStmt.setInt(4, player.getScore());
                    resultStmt.setInt(5, k.getFood());
                    resultStmt.setInt(6, k.getGold());
                    resultStmt.setBoolean(7, winners.contains(k));
                    resultStmt.executeUpdate();
                }
            }

            conn.commit();
            GameLogger.log("✅ Game data + results saved to database.");
            return gameId;

        } catch (SQLException e) {
            GameLogger.logError("❌ Failed to save game data", e);
            return -1;
        }
    }


    public void dropTables() {
        String dropKingdoms = "DROP TABLE IF EXISTS kingdoms CASCADE;";
        String dropWinners = "DROP TABLE IF EXISTS winners CASCADE;";
        String dropGames = "DROP TABLE IF EXISTS games CASCADE;";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(dropKingdoms);
            stmt.executeUpdate(dropWinners);
            stmt.executeUpdate(dropGames);

            GameLogger.log("🧹 All tables dropped.");

        } catch (SQLException e) {
            GameLogger.logError("❌ Failed to drop tables", e);
        }
    }
}
