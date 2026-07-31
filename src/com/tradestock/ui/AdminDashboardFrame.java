package com.tradestock.ui;

import com.tradestock.dao.OrderDAO;
import com.tradestock.dao.StockDAO;
import com.tradestock.dao.TraderDAO;
import com.tradestock.model.Admin;
import com.tradestock.model.Order;
import com.tradestock.model.Stock;
import com.tradestock.model.Trader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private Admin admin;

    private StockDAO stockDAO;
    private TraderDAO traderDAO;
    private OrderDAO orderDAO;

    // Stock Management components
    private JTable stocksTable;
    private DefaultTableModel stocksTableModel;
    private JTextField addStockNameField;
    private JTextField addStockPriceField;
    private JTextField addStockQtyField;

    // Trader List components
    private JTable tradersTable;
    private DefaultTableModel tradersTableModel;

    // Orders Audit components
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;

    public AdminDashboardFrame(Admin admin) {
        this.admin = admin;
        this.stockDAO = new StockDAO();
        this.traderDAO = new TraderDAO();
        this.orderDAO = new OrderDAO();
        initUI();
        loadAllData();
    }

    private void initUI() {
        setTitle("Inventory Manager - Admin Dashboard (" + admin.getUsername() + ")");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.COLOR_BG);
        setLayout(new BorderLayout());

        // Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_SURFACE);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = UITheme.createHeaderLabel("Admin Workspace - Logged in as: " + admin.getUsername());
        JButton logoutBtn = UITheme.createDangerButton("Logout");
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);
        tabbedPane.setBackground(UITheme.COLOR_BG);
        tabbedPane.setForeground(UITheme.COLOR_TEXT_LIGHT);

        tabbedPane.addTab("Store Inventory Management", createStockManagementPanel());
        tabbedPane.addTab("Registered Buyers Directory", createTradersPanel());
        tabbedPane.addTab("System Sales & Order Audit", createOrdersAuditPanel());

        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStockManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Add Stock Form Card
        JPanel addCard = UITheme.createCardPanel();
        addCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));

        addCard.add(UITheme.createHeaderLabel("Add Inventory Item:"));
        addCard.add(UITheme.createLabel("Item Name:"));
        addStockNameField = UITheme.createTextField(12);
        addCard.add(addStockNameField);

        addCard.add(UITheme.createLabel("Unit Price ($):"));
        addStockPriceField = UITheme.createTextField(6);
        addCard.add(addStockPriceField);

        addCard.add(UITheme.createLabel("Quantity:"));
        addStockQtyField = UITheme.createTextField(6);
        addCard.add(addStockQtyField);

        JButton addBtn = UITheme.createSuccessButton("+ Add Item");
        addBtn.addActionListener(e -> handleAddStock());
        addCard.add(addBtn);

        // Stock Table
        String[] columns = {"Item ID", "Item Name", "Unit Price ($)", "Available Store Stock"};
        stocksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        stocksTable = new JTable(stocksTableModel);
        UITheme.styleTable(stocksTable);

        JScrollPane tableScroll = new JScrollPane(stocksTable);
        tableScroll.getViewport().setBackground(UITheme.COLOR_SURFACE);
        tableScroll.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD));

        // Action Toolbar (Edit / Delete / Refresh)
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        toolbar.setBackground(UITheme.COLOR_BG);

        JButton editBtn = UITheme.createPrimaryButton("Edit Selected Item");
        JButton deleteBtn = UITheme.createDangerButton("Delete Selected Item");
        JButton refreshBtn = UITheme.createButton("Refresh List", UITheme.COLOR_CARD);

        editBtn.addActionListener(e -> handleEditStock());
        deleteBtn.addActionListener(e -> handleDeleteStock());
        refreshBtn.addActionListener(e -> loadStocks());

        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        toolbar.add(refreshBtn);

        panel.add(addCard, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTradersPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel header = UITheme.createHeaderLabel("All Registered Buyers / Customers");
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        String[] columns = {"Buyer ID", "Full Name", "Username", "Age", "Account Balance ($)"};
        tradersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tradersTable = new JTable(tradersTableModel);
        UITheme.styleTable(tradersTable);

        JScrollPane scrollPane = new JScrollPane(tradersTable);
        scrollPane.getViewport().setBackground(UITheme.COLOR_SURFACE);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setBackground(UITheme.COLOR_BG);
        JButton refreshBtn = UITheme.createButton("Refresh Buyers List", UITheme.COLOR_CARD);
        refreshBtn.addActionListener(e -> loadTraders());
        toolbar.add(refreshBtn);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrdersAuditPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.COLOR_BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel header = UITheme.createHeaderLabel("All System Sales & Purchase Audit Logs");
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        String[] columns = {"Order ID", "Buyer", "Item Name", "Order Type", "Quantity", "Unit Price ($)", "Total Amount ($)", "Timestamp"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        ordersTable = new JTable(ordersTableModel);
        UITheme.styleTable(ordersTable);

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.getViewport().setBackground(UITheme.COLOR_SURFACE);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.COLOR_CARD));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        toolbar.setBackground(UITheme.COLOR_BG);
        JButton refreshBtn = UITheme.createButton("Refresh Audit Logs", UITheme.COLOR_CARD);
        refreshBtn.addActionListener(e -> loadOrders());
        toolbar.add(refreshBtn);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(toolbar, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAllData() {
        loadStocks();
        loadTraders();
        loadOrders();
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
    }

    private void loadTraders() {
        tradersTableModel.setRowCount(0);
        List<Trader> traders = traderDAO.getAllTraders();
        for (Trader t : traders) {
            tradersTableModel.addRow(new Object[]{
                    t.getId(),
                    t.getName(),
                    t.getUsername(),
                    t.getAge(),
                    String.format("$%.2f", t.getBalance())
            });
        }
    }

    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        List<Order> orders = orderDAO.getAllOrders();
        for (Order o : orders) {
            ordersTableModel.addRow(new Object[]{
                    o.getId(),
                    o.getTraderUsername(),
                    o.getStockName(),
                    o.getOrderType(),
                    o.getQuantity(),
                    String.format("%.2f", o.getPriceAtOrder()),
                    String.format("%.2f", o.getTotalAmount()),
                    o.getTimestamp()
            });
        }
    }

    private void handleAddStock() {
        String name = addStockNameField.getText().trim();
        String priceStr = addStockPriceField.getText().trim();
        String qtyStr = addStockQtyField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all item details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int qty = Integer.parseInt(qtyStr);

            if (price <= 0 || qty < 0) {
                JOptionPane.showMessageDialog(this, "Price must be > 0 and Quantity must be >= 0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = stockDAO.addStock(name, price, qty);
            if (success) {
                JOptionPane.showMessageDialog(this, "Item added successfully to inventory!", "Success", JOptionPane.INFORMATION_MESSAGE);
                addStockNameField.setText("");
                addStockPriceField.setText("");
                addStockQtyField.setText("");
                loadStocks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add item to inventory.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEditStock() {
        int selectedRow = stocksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stockId = (Integer) stocksTableModel.getValueAt(selectedRow, 0);
        String currentName = (String) stocksTableModel.getValueAt(selectedRow, 1);
        String currentPrice = (String) stocksTableModel.getValueAt(selectedRow, 2);
        int currentQty = Integer.parseInt(stocksTableModel.getValueAt(selectedRow, 3).toString());

        JTextField nameF = UITheme.createTextField(15); nameF.setText(currentName);
        JTextField priceF = UITheme.createTextField(15); priceF.setText(currentPrice);
        JTextField qtyF = UITheme.createTextField(15); qtyF.setText(String.valueOf(currentQty));

        JPanel dialogPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        dialogPanel.add(new JLabel("Item Name:")); dialogPanel.add(nameF);
        dialogPanel.add(new JLabel("Unit Price ($):")); dialogPanel.add(priceF);
        dialogPanel.add(new JLabel("Quantity:")); dialogPanel.add(qtyF);

        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Edit Item Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameF.getText().trim();
                double newPrice = Double.parseDouble(priceF.getText().trim());
                int newQty = Integer.parseInt(qtyF.getText().trim());

                if (newName.isEmpty() || newPrice <= 0 || newQty < 0) {
                    JOptionPane.showMessageDialog(this, "Invalid inputs. Name cannot be empty, price > 0, qty >= 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (stockDAO.updateStock(stockId, newName, newPrice, newQty)) {
                    JOptionPane.showMessageDialog(this, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStocks();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update item.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Price and Quantity must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteStock() {
        int selectedRow = stocksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stockId = (Integer) stocksTableModel.getValueAt(selectedRow, 0);
        String stockName = (String) stocksTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete item: " + stockName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (stockDAO.deleteStock(stockId)) {
                JOptionPane.showMessageDialog(this, "Item deleted from inventory.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStocks();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
