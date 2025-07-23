package org.Game.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBTest {



    public static void main(String[] args) {
        System.out.println("Starting the app...");

        DatabaseManager dbManager = new DatabaseManager();
        System.out.println("Calling createTables...");
        dbManager.createTables();
        System.out.println("createTables() called.");
        System.out.println("Starting DB connection test...");

        String url = "jdbc:postgresql://localhost:5432/realm_war_game";
        String user = "postgres";
        String password = "2005";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Connected to the database!");
        } catch (SQLException e) {
            System.out.println("❌ Connection error: " + e.getMessage());
        }

        System.out.println("Done.");
    }
}
