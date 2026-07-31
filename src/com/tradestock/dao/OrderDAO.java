package com.tradestock.dao;

import com.tradestock.db.DatabaseManager;
import com.tradestock.model.Order;
import com.tradestock.model.Stock;
import com.tradestock.model.Trader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public int getOwnedShares(int traderId, int stockId) {
        String sql = "SELECT " +
                "COALESCE(SUM(CASE WHEN order_type = 'BUY' THEN quantity ELSE -quantity END), 0) AS net_shares " +
                "FROM orders WHERE trader_id = ? AND stock_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, traderId);
            pstmt.setInt(2, stockId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("net_shares");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String executeTrade(int traderId, int stockId, String orderType, int quantity) {
        TraderDAO traderDAO = new TraderDAO();
        StockDAO stockDAO = new StockDAO();

        Trader trader = traderDAO.getTraderById(traderId);
        if (trader == null) {
            return "Error: Buyer account not found.";
        }

        Stock stock = stockDAO.getStockById(stockId);
        if (stock == null) {
            return "Error: Inventory item not found.";
        }

        if (quantity <= 0) {
            return "Error: Quantity must be at least 1.";
        }

        double totalCost = quantity * stock.getPrice();

        if ("BUY".equalsIgnoreCase(orderType)) {
            if (stock.getQuantity() < quantity) {
                return "Error: Insufficient store inventory available (Available: " + stock.getQuantity() + " units).";
            }
            if (trader.getBalance() < totalCost) {
                return String.format("Error: Insufficient account balance. Required: $%.2f, Available: $%.2f", totalCost, trader.getBalance());
            }
        } else if ("SELL".equalsIgnoreCase(orderType)) {
            int owned = getOwnedShares(traderId, stockId);
            if (owned < quantity) {
                return String.format("Error: Cannot sell %d units. You currently own %d units of %s.", quantity, owned, stock.getName());
            }
        } else {
            return "Error: Invalid order type. Must be BUY or SELL.";
        }

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            // 1. Update Stock Quantity
            int newStockQty = "BUY".equalsIgnoreCase(orderType) ? (stock.getQuantity() - quantity) : (stock.getQuantity() + quantity);
            String updateStockSql = "UPDATE stocks SET quantity = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateStockSql)) {
                pstmt.setInt(1, newStockQty);
                pstmt.setInt(2, stockId);
                pstmt.executeUpdate();
            }

            // 2. Update Trader Balance
            double newBalance = "BUY".equalsIgnoreCase(orderType) ? (trader.getBalance() - totalCost) : (trader.getBalance() + totalCost);
            String updateTraderSql = "UPDATE traders SET balance = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateTraderSql)) {
                pstmt.setDouble(1, newBalance);
                pstmt.setInt(2, traderId);
                pstmt.executeUpdate();
            }

            // 3. Log Order
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String insertOrderSql = "INSERT INTO orders (trader_id, stock_id, order_type, quantity, price_at_order, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql)) {
                pstmt.setInt(1, traderId);
                pstmt.setInt(2, stockId);
                pstmt.setString(3, orderType.toUpperCase());
                pstmt.setInt(4, quantity);
                pstmt.setDouble(5, stock.getPrice());
                pstmt.setString(6, timestamp);
                pstmt.executeUpdate();
            }

            conn.commit();
            return "SUCCESS";
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "Database Error: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Order> getOrdersByTrader(int traderId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, s.name AS stock_name, t.username AS trader_username " +
                "FROM orders o " +
                "JOIN stocks s ON o.stock_id = s.id " +
                "JOIN traders t ON o.trader_id = t.id " +
                "WHERE o.trader_id = ? " +
                "ORDER BY o.id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, traderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapOrder(rs);
                    order.setStockName(rs.getString("stock_name"));
                    order.setTraderUsername(rs.getString("trader_username"));
                    list.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, s.name AS stock_name, t.username AS trader_username " +
                "FROM orders o " +
                "LEFT JOIN stocks s ON o.stock_id = s.id " +
                "LEFT JOIN traders t ON o.trader_id = t.id " +
                "ORDER BY o.id DESC";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapOrder(rs);
                order.setStockName(rs.getString("stock_name") != null ? rs.getString("stock_name") : "Deleted Item (#" + rs.getInt("stock_id") + ")");
                order.setTraderUsername(rs.getString("trader_username") != null ? rs.getString("trader_username") : "Deleted Buyer (#" + rs.getInt("trader_id") + ")");
                list.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("id"),
                rs.getInt("trader_id"),
                rs.getInt("stock_id"),
                rs.getString("order_type"),
                rs.getInt("quantity"),
                rs.getDouble("price_at_order"),
                rs.getString("timestamp")
        );
    }
}
