package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class FinancesPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel tableModel;

    public FinancesPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 15));
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JLabel header = new JLabel("Financial Overview");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(ThemeManager.BG_MAIN);

        // Summary cards
        JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        cardsRow.setBackground(ThemeManager.BG_MAIN);

        double totalIncome = getTotal("income");
        double totalExpense = getTotal("expense");
        double balance = totalIncome - totalExpense;

        cardsRow.add(ThemeManager.createStatCard("Total Income", "KES " + String.format("%,.2f", totalIncome), ThemeManager.SUCCESS));
        cardsRow.add(ThemeManager.createStatCard("Total Expenses", "KES " + String.format("%,.2f", totalExpense), ThemeManager.DANGER));

        Color balanceColor = balance >= 0 ? ThemeManager.PRIMARY : ThemeManager.DANGER;
        cardsRow.add(ThemeManager.createStatCard("Balance", "KES " + String.format("%,.2f", balance), balanceColor));

        centerPanel.add(cardsRow, BorderLayout.NORTH);

        // Transaction history table
        JPanel tableSection = new JPanel(new BorderLayout(0, 10));
        tableSection.setBackground(ThemeManager.BG_MAIN);

        JLabel tableTitle = new JLabel("Transaction History");
        tableTitle.setFont(ThemeManager.FONT_SUBHEADING);
        tableTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        tableSection.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Type", "Description", "Amount (KES)", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(ThemeManager.FONT_BODY);
        table.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        table.setGridColor(ThemeManager.BORDER);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Center align amount and date
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        // Color code the type column
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    String type = (String) value;
                    if ("income".equalsIgnoreCase(type)) {
                        setForeground(ThemeManager.SUCCESS);
                    } else {
                        setForeground(ThemeManager.DANGER);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        tableSection.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableSection, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadTransactions();
    }

    private double getTotal(String type) {
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE type = ?"
            );
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total: " + e.getMessage());
        }
        return 0;
    }

    private void loadTransactions() {
        tableModel.setRowCount(0);
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM transactions ORDER BY transaction_date DESC");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("type").toUpperCase(),
                    rs.getString("description"),
                    String.format("%,.2f", rs.getDouble("amount")),
                    rs.getString("category"),
                    rs.getString("transaction_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}