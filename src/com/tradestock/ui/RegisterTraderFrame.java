package com.tradestock.ui;

import com.tradestock.dao.TraderDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterTraderFrame extends JFrame {

    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField ageField;
    private JTextField balanceField;

    private TraderDAO traderDAO;

    public RegisterTraderFrame() {
        traderDAO = new TraderDAO();
        initUI();
    }

    private void initUI() {
        setTitle("Inventory Manager - Buyer Registration");
        setSize(480, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.COLOR_BG);
        setLayout(new BorderLayout());

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UITheme.COLOR_BG);
        rootPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Title Panel
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setBackground(UITheme.COLOR_BG);
        JLabel titleLabel = UITheme.createTitleLabel("Buyer Registration");
        JLabel subtitleLabel = UITheme.createLabel("Create your buyer account (Must be 18+)");
        subtitleLabel.setForeground(UITheme.COLOR_TEXT_MUTED);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // Form Card Panel
        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = UITheme.createTextField(18);
        usernameField = UITheme.createTextField(18);
        passwordField = UITheme.createPasswordField(18);
        ageField = UITheme.createTextField(18);
        balanceField = UITheme.createTextField(18);
        balanceField.setText("10000.00");

        addFormField(formCard, gbc, 0, "Full Name:", nameField);
        addFormField(formCard, gbc, 1, "Username:", usernameField);
        addFormField(formCard, gbc, 2, "Password:", passwordField);
        addFormField(formCard, gbc, 3, "Age (18+):", ageField);
        addFormField(formCard, gbc, 4, "Starting Balance ($):", balanceField);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(UITheme.COLOR_BG);
        btnPanel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton cancelBtn = UITheme.createButton("Back to Login", UITheme.COLOR_CARD);
        JButton registerBtn = UITheme.createSuccessButton("Register Account");

        cancelBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        registerBtn.addActionListener(e -> handleRegister());

        btnPanel.add(cancelBtn);
        btnPanel.add(registerBtn);

        rootPanel.add(titlePanel, BorderLayout.NORTH);
        rootPanel.add(formCard, BorderLayout.CENTER);
        rootPanel.add(btnPanel, BorderLayout.SOUTH);

        add(rootPanel);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent inputComponent) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(UITheme.createLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(inputComponent, gbc);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String ageStr = ageField.getText().trim();
        String balanceStr = balanceField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || ageStr.isEmpty() || balanceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all registration fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid integer number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Preserve rule: Age must be 18+
        if (age < 18) {
            JOptionPane.showMessageDialog(this, "Registration Rejected: You must be at least 18 years old to register.", "Age Restriction", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double balance;
        try {
            balance = Double.parseDouble(balanceStr);
            if (balance < 0) {
                JOptionPane.showMessageDialog(this, "Starting balance cannot be negative.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Balance must be a valid decimal number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (traderDAO.existsUsername(username)) {
            JOptionPane.showMessageDialog(this, "Username '" + username + "' is already taken. Please choose another.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = traderDAO.register(name, username, password, age, balance);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in with your credentials.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register account due to a database error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
