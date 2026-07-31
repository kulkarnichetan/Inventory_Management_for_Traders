package com.tradestock.ui;

import com.tradestock.dao.AdminDAO;
import com.tradestock.dao.TraderDAO;
import com.tradestock.model.Admin;
import com.tradestock.model.Trader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JRadioButton adminRadio;
    private JRadioButton traderRadio;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private AdminDAO adminDAO;
    private TraderDAO traderDAO;

    public LoginFrame() {
        adminDAO = new AdminDAO();
        traderDAO = new TraderDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Inventory Manager - Login");
        setSize(440, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.COLOR_BG);
        setLayout(new BorderLayout());

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.COLOR_BG);
        rootPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UITheme.COLOR_BG);
        JLabel titleLabel = UITheme.createTitleLabel("Inventory Manager");
        JLabel subtitleLabel = UITheme.createLabel("Local Store Inventory & Order System");
        subtitleLabel.setForeground(UITheme.COLOR_TEXT_MUTED);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // Form Card Panel
        JPanel cardPanel = UITheme.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Role Selection
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        rolePanel.setBackground(UITheme.COLOR_SURFACE);
        traderRadio = new JRadioButton("Trader / Buyer", true);
        adminRadio = new JRadioButton("Admin", false);
        
        traderRadio.setFont(UITheme.FONT_BOLD);
        traderRadio.setForeground(UITheme.COLOR_TEXT_LIGHT);
        traderRadio.setBackground(UITheme.COLOR_SURFACE);

        adminRadio.setFont(UITheme.FONT_BOLD);
        adminRadio.setForeground(UITheme.COLOR_TEXT_LIGHT);
        adminRadio.setBackground(UITheme.COLOR_SURFACE);

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(traderRadio);
        roleGroup.add(adminRadio);

        rolePanel.add(traderRadio);
        rolePanel.add(adminRadio);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        cardPanel.add(UITheme.createLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(rolePanel, gbc);

        // Username Field
        usernameField = UITheme.createTextField(18);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        cardPanel.add(UITheme.createLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(usernameField, gbc);

        // Password Field
        passwordField = UITheme.createPasswordField(18);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        cardPanel.add(UITheme.createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        cardPanel.add(passwordField, gbc);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        actionPanel.setBackground(UITheme.COLOR_BG);
        actionPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton loginBtn = UITheme.createPrimaryButton("Login");
        JButton registerBtn = UITheme.createButton("New Buyer? Register Here", UITheme.COLOR_CARD);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            new RegisterTraderFrame().setVisible(true);
            dispose();
        });

        actionPanel.add(loginBtn);
        actionPanel.add(registerBtn);

        rootPanel.add(headerPanel, BorderLayout.NORTH);
        rootPanel.add(cardPanel, BorderLayout.CENTER);
        rootPanel.add(actionPanel, BorderLayout.SOUTH);

        add(rootPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (adminRadio.isSelected()) {
            Admin admin = adminDAO.authenticate(username, password);
            if (admin != null) {
                JOptionPane.showMessageDialog(this, "Welcome Admin, " + admin.getUsername() + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                new AdminDashboardFrame(admin).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Admin credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Trader trader = traderDAO.authenticate(username, password);
            if (trader != null) {
                JOptionPane.showMessageDialog(this, "Welcome back, " + trader.getName() + "!", "Login Success", JOptionPane.INFORMATION_MESSAGE);
                new TraderDashboardFrame(trader).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Trader/Buyer credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
