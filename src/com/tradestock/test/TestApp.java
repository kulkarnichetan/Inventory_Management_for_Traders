package com.tradestock.test;

import com.tradestock.dao.AdminDAO;
import com.tradestock.dao.OrderDAO;
import com.tradestock.dao.StockDAO;
import com.tradestock.dao.TraderDAO;
import com.tradestock.db.DatabaseManager;
import com.tradestock.model.Admin;
import com.tradestock.model.Order;
import com.tradestock.model.Stock;
import com.tradestock.model.Trader;

import java.util.List;

public class TestApp {
    public static void main(String[] args) {
        System.out.println("--- Starting TradeStock Manager Integration Test ---");

        // 1. Initialize Database
        DatabaseManager.initializeDatabase();

        AdminDAO adminDAO = new AdminDAO();
        TraderDAO traderDAO = new TraderDAO();
        StockDAO stockDAO = new StockDAO();
        OrderDAO orderDAO = new OrderDAO();

        // 2. Verify Admin Login
        Admin admin = adminDAO.authenticate("admin", "admin123");
        assert admin != null : "Default admin login failed!";
        System.out.println("✔ Admin Login Success: " + admin.getUsername());

        // 3. Register Trader with Age Check
        String testUser = "trader_test_" + System.currentTimeMillis();
        boolean regSuccess = traderDAO.register("John Doe", testUser, "pass123", 25, 10000.0);
        assert regSuccess : "Trader registration failed!";
        System.out.println("✔ Trader Registration Success: " + testUser);

        Trader trader = traderDAO.authenticate(testUser, "pass123");
        assert trader != null : "Trader authentication failed!";
        assert trader.getAge() >= 18 : "Age rule violated!";
        System.out.println("✔ Trader Authenticated & Age Check Passed (Age: " + trader.getAge() + ")");

        // 4. Get Stocks
        List<Stock> stocks = stockDAO.getAllStocks();
        assert !stocks.isEmpty() : "Stock inventory is empty!";
        Stock apple = stocks.get(0);
        System.out.println("✔ Available Market Stock: " + apple.getName() + " | Price: $" + apple.getPrice() + " | Qty: " + apple.getQuantity());

        // 5. Test BUY Order
        int buyQty = 5;
        double initialBalance = trader.getBalance();
        int initialStockQty = apple.getQuantity();

        String buyResult = orderDAO.executeTrade(trader.getId(), apple.getId(), "BUY", buyQty);
        assert "SUCCESS".equalsIgnoreCase(buyResult) : "BUY order failed: " + buyResult;

        Trader updatedTrader = traderDAO.getTraderById(trader.getId());
        Stock updatedApple = stockDAO.getStockById(apple.getId());

        double expectedBalance = initialBalance - (buyQty * apple.getPrice());
        assert Math.abs(updatedTrader.getBalance() - expectedBalance) < 0.01 : "Trader balance calculation incorrect!";
        assert updatedApple.getQuantity() == (initialStockQty - buyQty) : "Stock quantity reduction incorrect!";
        System.out.println("✔ BUY Trade Execution Success! New Balance: $" + updatedTrader.getBalance() + " | Remaining Stock Qty: " + updatedApple.getQuantity());

        // 6. Test Insufficient Balance Failure
        String overBuyResult = orderDAO.executeTrade(trader.getId(), apple.getId(), "BUY", 1000000);
        assert overBuyResult.startsWith("Error:") : "Overbuy test failed to reject!";
        System.out.println("✔ Insufficient Balance Validation Success: " + overBuyResult);

        // 7. Test SELL Order
        int sellQty = 2;
        String sellResult = orderDAO.executeTrade(trader.getId(), apple.getId(), "SELL", sellQty);
        assert "SUCCESS".equalsIgnoreCase(sellResult) : "SELL order failed: " + sellResult;

        Trader traderAfterSell = traderDAO.getTraderById(trader.getId());
        Stock appleAfterSell = stockDAO.getStockById(apple.getId());
        assert traderAfterSell.getBalance() > updatedTrader.getBalance() : "Balance did not increase on sell!";
        assert appleAfterSell.getQuantity() == (updatedApple.getQuantity() + sellQty) : "Stock qty did not increase on sell!";
        System.out.println("✔ SELL Trade Execution Success! New Balance: $" + traderAfterSell.getBalance() + " | Stock Qty: " + appleAfterSell.getQuantity());

        // 8. Test Oversell Unowned Shares Failure
        String overSellResult = orderDAO.executeTrade(trader.getId(), apple.getId(), "SELL", 100);
        assert overSellResult.startsWith("Error:") : "Oversell test failed to reject!";
        System.out.println("✔ Unowned Share Validation Success: " + overSellResult);

        // 9. Order History Check
        List<Order> history = orderDAO.getOrdersByTrader(trader.getId());
        assert history.size() == 2 : "Order history count mismatch!";
        System.out.println("✔ Order History Logging Verified: " + history.size() + " orders recorded with timestamps.");

        System.out.println("\n*** ALL INTEGRATION TESTS PASSED PERFECTLY! ***");
    }
}
