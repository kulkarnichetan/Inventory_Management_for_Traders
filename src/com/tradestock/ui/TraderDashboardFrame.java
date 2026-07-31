package com.tradestock.ui;

import com.tradestock.dao.OrderDAO;
import com.tradestock.dao.StockDAO;
import com.tradestock.dao.TraderDAO;
import com.tradestock.model.Order;
import com.tradestock.model.Stock;
import com.tradestock.model.Trader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TraderDashboardFrame extends JFrame {

    private Trader trader;

    private StockDAO stockDAO;
    private TraderDAO traderDAO;
    private OrderDAO orderDAO;

    private JLabel balanceLabel;
    private JLabel welcomeLabel;

    // Available stocks table
    private JTable stocksTable;
    private DefaultTableModel stocksTableModel;

    // Order History table
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Trade form controls
    private JLabel selectedStockLabel;
    private JLabel selectedPriceLabel;
    private JLabel ownedSharesLabel;
    private JTextField quantityField;
    private JComboBox<String> orderTypeCombo;
    private JLabel totalCostLabel;

    private Stock currentSelectedStock = null;

    public TraderDashboardFrame(Trader trader) {
        this.trader = trader;
        this.stockDAO = new StockDAO();
        this.traderDAO = new TraderDAO();
        this.orderDAO = new OrderDAO();
        initUI();
        refreshTraderData();
        loadStocks();
        loadHistory();
    }

    private void initUI() {
        setTitle("Inventory Manager - Buyer Dashboard (" + trader.getName() + ")");
        setSize(980, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.COLOR_BG);
        setLayout(new BorderLayout());

        // Header Panel with Balance Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_SURFACE);
        headerPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        welcomeLabel = UITheme.createHeaderLabel("Welcome, " + trader.getName() + " (@" + trader.getUsername() + ")");
        
        balanceLabel = new JLabel(String.format("Available Balance: $%.2f", trader.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balanceLabel.setForeground(UITheme.COLOR_SUCCESS);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setBackground(UITheme.COLOR_SURFACE);
        rightHeader.add(balanceLabel);

        JButton logoutBtn = UITheme.createDangerButton("Logout");
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        rightHeader.add(logoutBtn);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        // Main Content - Split into Top (Catalog & Order Form) and Bottom (Order History)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.COLOR_BG);
        tabbedPane.setForeground(UITheme.COLOR_TEXT_LIGHT);

        tabbedPane.addTab("Store Catalog & Orders", createTradingPanel());
        tabbedPane.addTab("Order & Purchase History", createHistoryPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTradingPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(UITheme.COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Available Stocks Table (Left/Center)
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBackground(UITheme.COLOR_BG);

        JLabel tableHeader = UITheme.createHeaderLabel("Available Store Inventory");
        tableHeader.setBorder(new EmptyBorder(0, 0, 8, 0));

        String[] columns = {"ID", "Item Name", "Unit Price ($)", "Store Stock Qty"};
        stocksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        stocksTable = new JTable(stocksTableModel);
        UITheme.styleTable(stocksTable);

        stocksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleStockSelection();
            }
        });

        JScrollPane scrollPane = new JScrollPane(stocksTable);
        scrollPane.getViewport().setBackground(UITheme.COLOR_SURFACE);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD));

        tablePanel.add(tableHeader, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Order Execution Form Card (Right Panel)
        JPanel tradeCard = UITheme.createCardPanel();
        tradeCard.setPreferredSize(new Dimension(320, 0));
        tradeCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        int row = 0;
        JLabel cardTitle = UITheme.createHeaderLabel("Place Order");
        gbc.gridy = row++; gbc.gridwidth = 2;
        tradeCard.add(cardTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = row; gbc.gridx = 0; tradeCard.add(UITheme.createLabel("Selected Item:"), gbc);
        selectedStockLabel = UITheme.createLabel("None Selected");
        selectedStockLabel.setFont(UITheme.FONT_BOLD);
        gbc.gridx = 1; tradeCard.add(selectedStockLabel, gbc);

        row++;
        gbc.gridy = row; gbc.gridx = 0; tradeCard.add(UITheme.createLabel("Unit Price:"), gbc);
        selectedPriceLabel = UITheme.createLabel("$0.00");
        selectedPriceLabel.setFont(UITheme.FONT_BOLD);
        gbc.gridx = 1; tradeCard.add(selectedPriceLabel, gbc);

        row++;
        gbc.gridy = row; gbc.gridx = 0; tradeCard.add(UITheme.createLabel("Purchased Units:"), gbc);
        ownedSharesLabel = UITheme.createLabel("0 units");
        ownedSharesLabel.setFont(UITheme.FONT_BOLD);
        gbc.gridx = 1; tradeCard.add(ownedSharesLabel, gbc);

        row++;
        gbc.gridy = row; gbc.gridx = 0; tradeCard.add(UITheme.createLabel("Order Type:"), gbc);
        orderTypeCombo = new JComboBox<>(new String[]{"BUY", "SELL"});
        orderTypeCombo.setFont(UITheme.FONT_BOLD);
        orderTypeCombo.setBackground(UITheme.COLOR_SURFACE);
        orderTypeCombo.setForeground(UITheme.COLOR_TEXT_LIGHT);
        orderTypeCombo.addActionListener(e -> updateCalculatedTotal());
        gbc.gridx = 1; tradeCard.add(orderTypeCombo, gbc);

        row++;
        gbc.gridy = row; gbc.gridx = 0; tradeCard.add(UITheme.createLabel("Quantity:"), gbc);
        quantityField = UITheme.createTextField(8);
        quantityField.setText("1");
        quantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateCalculatedTotal();
            }
        });
        gbc.gridx = 1; tradeCard.add(quantityField, gbc);

        row++;
        gbc.gridy = row; gbc.gridx = 0; tradeCard.add(UITheme.createLabel("Total Order Value:"), gbc);
        totalCostLabel = new JLabel("$0.00");
        totalCostLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalCostLabel.setForeground(UITheme.COLOR_PRIMARY);
        gbc.gridx = 1; tradeCard.add(totalCostLabel, gbc);

        row++;
        gbc.gridy = row; gbc.gridwidth = 2; gbc.gridx = 0;
        JButton executeTradeBtn = UITheme.createSuccessButton("Confirm Order");
        executeTradeBtn.addActionListener(e -> handleExecuteTrade());
        tradeCard.add(executeTradeBtn, gbc);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(tradeCard, BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel header = UITheme.createHeaderLabel("My Order & Purchase History");
        header.setBorder(new EmptyBorder(0, 0, 8, 0));

        String[] columns = {"Order ID", "Item Name", "Type", "Quantity", "Unit Price ($)", "Total ($)", "Timestamp"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        UITheme.styleTable(historyTable);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(UITheme.COLOR_SURFACE);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setBackground(UITheme.COLOR_BG);
        JButton refreshBtn = UITheme.createButton("Refresh History", UITheme.COLOR_CARD);
        refreshBtn.addActionListener(e -> loadHistory());
        toolbar.add(refreshBtn);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshTraderData() {
        Trader updated = traderDAO.getTraderById(trader.getId());
        if (updated != null) {
            this.trader = updated;
            balanceLabel.setText(String.format("Available Balance: $%.2f", trader.getBalance()));
        }
    }

    private void loadStocks() {
        stocksTableModel.setRowCount(0);
        List<Stock> stocks = stockDAO.getAllStocks();
        for (Stock s : stocks) {
            stocksTableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    String.format("%.2f", s.getPrice()),
                    s.getQuantity()
            });
        }
        if (currentSelectedStock != null) {
            updateCalculatedTotal();
        }
    }

    private void loadHistory() {
        historyTableModel.setRowCount(0);
        List<Order> orders = orderDAO.getOrdersByTrader(trader.getId());
        for (Order o : orders) {
            historyTableModel.addRow(new Object[]{
                    o.getId(),
                    o.getStockName(),
                    o.getOrderType(),
                    o.getQuantity(),
                    String.format("%.2f", o.getPriceAtOrder()),
                    String.format("%.2f", o.getTotalAmount()),
                    o.getTimestamp()
            });
        }
    }

    private void handleStockSelection() {
        int row = stocksTable.getSelectedRow();
        if (row != -1) {
            int stockId = (Integer) stocksTableModel.getValueAt(row, 0);
            currentSelectedStock = stockDAO.getStockById(stockId);
            if (currentSelectedStock != null) {
                selectedStockLabel.setText(currentSelectedStock.getName());
                selectedPriceLabel.setText(String.format("$%.2f", currentSelectedStock.getPrice()));
                
                int owned = orderDAO.getOwnedShares(trader.getId(), stockId);
                ownedSharesLabel.setText(owned + " units");

                updateCalculatedTotal();
            }
        }
    }

    private void updateCalculatedTotal() {
        if (currentSelectedStock == null) {
            totalCostLabel.setText("$0.00");
            return;
        }
        try {
            int qty = Integer.parseInt(quantityField.getText().trim());
            if (qty > 0) {
                double total = qty * currentSelectedStock.getPrice();
                totalCostLabel.setText(String.format("$%.2f", total));
            } else {
                totalCostLabel.setText("$0.00");
            }
        } catch (NumberFormatException e) {
            totalCostLabel.setText("$0.00");
        }
    }

    private void handleExecuteTrade() {
        if (currentSelectedStock == null) {
            JOptionPane.showMessageDialog(this, "Please select an inventory item from the table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtyStr = quantityField.getText().trim();
        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be at least 1.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer quantity.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String orderType = (String) orderTypeCombo.getSelectedItem();

        // Perform trade execution
        String result = orderDAO.executeTrade(trader.getId(), currentSelectedStock.getId(), orderType, quantity);

        if ("SUCCESS".equalsIgnoreCase(result)) {
            JOptionPane.showMessageDialog(this, "Order Processed Successfully!\n" + orderType + " " + quantity + " units of " + currentSelectedStock.getName(), "Order Successful", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh data dynamically
            refreshTraderData();
            loadStocks();
            loadHistory();
            handleStockSelection(); // refresh selection info
        } else {
            JOptionPane.showMessageDialog(this, result, "Order Processing Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
