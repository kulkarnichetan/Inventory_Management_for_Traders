package com.tradestock;

import com.tradestock.db.DatabaseManager;
import com.tradestock.ui.LoginFrame;
import com.tradestock.ui.UITheme;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Apply global Swing theme
        UITheme.applyGlobalTheme();

        // 2. Auto-initialize database schema & seed defaults on first run
        System.out.println("[TradeStock Manager] Initializing Database...");
        DatabaseManager.initializeDatabase();

        // 3. Launch UI on Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            System.out.println("[TradeStock Manager] Launching UI...");
            new LoginFrame().setVisible(true);
        });
    }
}
