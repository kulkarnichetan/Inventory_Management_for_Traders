package com.tradestock.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tradestock.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found in classpath.", e);
        }
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create Traders Table
            stmt.execute("CREATE TABLE IF NOT EXISTS traders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "age INTEGER NOT NULL, " +
                    "balance REAL NOT NULL DEFAULT 10000.0" +
                    ");");

            // Create Admins Table
            stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL" +
                    ");");

            // Create Stocks Table
            stmt.execute("CREATE TABLE IF NOT EXISTS stocks (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "quantity INTEGER NOT NULL" +
                    ");");

            // Create Orders Table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "trader_id INTEGER NOT NULL, " +
                    "stock_id INTEGER NOT NULL, " +
                    "order_type TEXT NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "price_at_order REAL NOT NULL, " +
                    "timestamp TEXT NOT NULL, " +
                    "FOREIGN KEY(trader_id) REFERENCES traders(id), " +
                    "FOREIGN KEY(stock_id) REFERENCES stocks(id)" +
                    ");");

            // Seed default admin if none exists
            ResultSet rsAdmin = stmt.executeQuery("SELECT COUNT(*) FROM admins;");
            if (rsAdmin.next() && rsAdmin.getInt(1) == 0) {
                stmt.execute("INSERT INTO admins (username, password) VALUES ('admin', 'admin123');");
                System.out.println("[DB] Seeded default admin account (username: admin, password: admin123)");
            }

            // Seed default shop inventory if none exists
            ResultSet rsStock = stmt.executeQuery("SELECT COUNT(*) FROM stocks;");
            if (rsStock.next() && rsStock.getInt(1) == 0) {
                stmt.execute("INSERT INTO stocks (name, price, quantity) VALUES ('Wheat Flour (Atta 30kg Bag)', 24.50, 100);");
                stmt.execute("INSERT INTO stocks (name, price, quantity) VALUES ('Basmati Rice (25kg Bag)', 35.00, 80);");
                stmt.execute("INSERT INTO stocks (name, price, quantity) VALUES ('Refined Sugar (10kg Pack)', 12.00, 150);");
                stmt.execute("INSERT INTO stocks (name, price, quantity) VALUES ('Sunflower Cooking Oil (5L Can)', 18.50, 60);");
                stmt.execute("INSERT INTO stocks (name, price, quantity) VALUES ('Pulses & Lentils (Toor Dal 10kg)', 16.00, 120);");
                stmt.execute("INSERT INTO stocks (name, price, quantity) VALUES ('Whole Spices Mix (2kg Box)', 15.00, 90);");
                System.out.println("[DB] Seeded default shop inventory.");
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
