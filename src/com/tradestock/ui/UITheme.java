package com.tradestock.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {
    public static final Color COLOR_BG = new Color(15, 23, 42);         // #0f172a
    public static final Color COLOR_SURFACE = new Color(30, 41, 59);    // #1e293b
    public static final Color COLOR_CARD = new Color(51, 65, 85);       // #334155
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);   // #2563eb
    public static final Color COLOR_PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color COLOR_SUCCESS = new Color(22, 163, 74);   // #16a34a
    public static final Color COLOR_DANGER = new Color(220, 38, 38);    // #dc2626
    public static final Color COLOR_TEXT_LIGHT = new Color(248, 250, 252);
    public static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(COLOR_TEXT_LIGHT);
        return label;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADER);
        label.setForeground(COLOR_TEXT_LIGHT);
        return label;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(COLOR_TEXT_LIGHT);
        return label;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setBackground(COLOR_SURFACE);
        tf.setForeground(COLOR_TEXT_LIGHT);
        tf.setCaretColor(COLOR_TEXT_LIGHT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setBackground(COLOR_SURFACE);
        pf.setForeground(COLOR_TEXT_LIGHT);
        pf.setCaretColor(COLOR_TEXT_LIGHT);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        return pf;
    }

    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, COLOR_PRIMARY);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, COLOR_SUCCESS);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, COLOR_DANGER);
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setBackground(COLOR_SURFACE);
        table.setForeground(COLOR_TEXT_LIGHT);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(COLOR_CARD);
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(COLOR_CARD);
        header.setForeground(COLOR_TEXT_LIGHT);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? COLOR_SURFACE : COLOR_CARD);
                    c.setForeground(COLOR_TEXT_LIGHT);
                }
                return c;
            }
        });
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }
}
