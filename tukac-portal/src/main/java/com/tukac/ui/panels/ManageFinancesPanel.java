package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class ManageFinancesPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable transactionsTable;

    private JComboBox<String> typeCombo;
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JTextField dateField;

    public ManageFinancesPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.BG_MAIN);

        // Header with summary
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setBackground(ThemeManager.BG_MAIN);

        JLabel header = new JLabel("Manage Finances");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        headerPanel.add(header, BorderLayout.NORTH);

        // Summary cards
        JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        cardsRow.setBackground(ThemeManager.BG_MAIN);

        double totalIncome = getTotal("income");
        double totalExpense = getTotal("expense");
        double balance = totalIncome - totalExpense;

        cardsRow.add(ThemeManager.createStatCard("Income", "KES " + String.format("%,.2f", totalIncome), ThemeManager.SUCCESS));
        cardsRow.add(ThemeManager.createStatCard("Expenses", "KES " + String.format("%,.2f", totalExpense), ThemeManager.DANGER));
        cardsRow.add(ThemeManager.createStatCard("Balance", "KES " + String.format("%,.2f", balance), ThemeManager.PRIMARY));

        headerPanel.add(cardsRow, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(340);
        splitPane.setBorder(null);

        // ===== FORM =====
        JPanel formCard = ThemeManager.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Transaction Details");
        formTitle.setFont(ThemeManager.FONT_SUBHEADING);
        formTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(15));

        // Type dropdown
        addFormLabel(formCard, "Type");
        typeCombo = new JComboBox<>(new String[]{"income", "expense"});
        typeCombo.setFont(ThemeManager.FONT_BODY);
        typeCombo.setMaximumSize(new Dimension(300, 36));
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(typeCombo);
        formCard.add(Box.createVerticalStrut(10));

        // Description
        addFormLabel(formCard, "Description");
        descriptionField = ThemeManager.createTextField(20);
        descriptionField.setMaximumSize(new Dimension(300, 36));
        descriptionField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(descriptionField);
        formCard.add(Box.createVerticalStrut(10));

        // Amount
        addFormLabel(formCard, "Amount (KES)");
        amountField = ThemeManager.createTextField(20);
        amountField.setMaximumSize(new Dimension(300, 36));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(amountField);
        formCard.add(Box.createVerticalStrut(10));

        // Category dropdown
        addFormLabel(formCard, "Category");
        categoryCombo = new JComboBox<>(new String[]{
            "Donations", "Membership Fees", "Fundraising", "Sponsorship",
            "Supplies", "Venue", "Transport", "Catering", "Printing", "Other"
        });
        categoryCombo.setFont(ThemeManager.FONT_BODY);
        categoryCombo.setMaximumSize(new Dimension(300, 36));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(categoryCombo);
        formCard.add(Box.createVerticalStrut(10));

        // Date
        addFormLabel(formCard, "Date (YYYY-MM-DD)");
        dateField = ThemeManager.createTextField(20);
        dateField.setMaximumSize(new Dimension(300, 36));
        dateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(dateField);
        formCard.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(ThemeManager.BG_CARD);
        btnPanel.setMaximumSize(new Dimension(300, 45));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addBtn = ThemeManager.createButton("Add", ThemeManager.SUCCESS);
        addBtn.addActionListener(e -> addTransaction());
        btnPanel.add(addBtn);

        JButton updateBtn = ThemeManager.createButton("Update", ThemeManager.PRIMARY);
        updateBtn.addActionListener(e -> updateTransaction());
        btnPanel.add(updateBtn);

        JButton deleteBtn = ThemeManager.createButton("Delete", ThemeManager.DANGER);
        deleteBtn.addActionListener(e -> deleteTransaction());
        btnPanel.add(deleteBtn);

        JButton clearBtn = ThemeManager.createButton("Clear", new Color(107, 114, 128));
        clearBtn.addActionListener(e -> clearForm());
        btnPanel.add(clearBtn);

        formCard.add(btnPanel);

        splitPane.setLeftComponent(formCard);

        // ===== TABLE =====
        String[] columns = {"ID", "Type", "Description", "Amount", "Category", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(32);
        transactionsTable.setFont(ThemeManager.FONT_BODY);
        transactionsTable.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        transactionsTable.setGridColor(ThemeManager.BORDER);
        transactionsTable.setSelectionBackground(new Color(219, 234, 254));
        transactionsTable.getColumnModel().getColumn(0).setMaxWidth(40);
        transactionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color code type
        transactionsTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    String type = (String) value;
                    if ("INCOME".equalsIgnoreCase(type)) {
                        setForeground(ThemeManager.SUCCESS);
                    } else {
                        setForeground(ThemeManager.DANGER);
                    }
                }
                return c;
            }
        });

        // Click row to populate form
        transactionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = transactionsTable.getSelectedRow();
                if (row >= 0) {
                    String type = ((String) tableModel.getValueAt(row, 1)).toLowerCase();
                    typeCombo.setSelectedItem(type);
                    descriptionField.setText((String) tableModel.getValueAt(row, 2));
                    amountField.setText(((String) tableModel.getValueAt(row, 3)).replace(",", ""));
                    categoryCombo.setSelectedItem(tableModel.getValueAt(row, 4));
                    dateField.setText((String) tableModel.getValueAt(row, 5));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(transactionsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        splitPane.setRightComponent(tableScroll);

        add(splitPane, BorderLayout.CENTER);

        loadTransactions();
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = ThemeManager.createLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
    }

    private double getTotal(String type) {
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "SELECT COALESCE(SUM(amount), 0) as total FROM transactions WHERE type = ?"
            );
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
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

    private void addTransaction() {
        if (descriptionField.getText().trim().isEmpty() || amountField.getText().trim().isEmpty() || dateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description, Amount, and Date are required.");
            return;
        }
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "INSERT INTO transactions (type, description, amount, category, transaction_date, created_by) VALUES (?,?,?,?,?,?)"
            );
            pstmt.setString(1, (String) typeCombo.getSelectedItem());
            pstmt.setString(2, descriptionField.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(amountField.getText().trim()));
            pstmt.setString(4, (String) categoryCombo.getSelectedItem());
            pstmt.setString(5, dateField.getText().trim());
            pstmt.setInt(6, currentUser.getId());
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaction added!");
            clearForm();
            refreshAll();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateTransaction() {
        int row = transactionsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a transaction to update."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE transactions SET type=?, description=?, amount=?, category=?, transaction_date=? WHERE id=?"
            );
            pstmt.setString(1, (String) typeCombo.getSelectedItem());
            pstmt.setString(2, descriptionField.getText().trim());
            pstmt.setDouble(3, Double.parseDouble(amountField.getText().trim()));
            pstmt.setString(4, (String) categoryCombo.getSelectedItem());
            pstmt.setString(5, dateField.getText().trim());
            pstmt.setInt(6, id);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Transaction updated!");
            clearForm();
            refreshAll();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteTransaction() {
        int row = transactionsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a transaction to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this transaction?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int id = (int) tableModel.getValueAt(row, 0);
        try {
            Statement stmt = Database.getConnection().createStatement();
            stmt.execute("DELETE FROM transactions WHERE id = " + id);
            JOptionPane.showMessageDialog(this, "Transaction deleted!");
            clearForm();
            refreshAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void refreshAll() {
        // Rebuild the whole panel to update the summary cards
        removeAll();
        ManageFinancesPanel fresh = new ManageFinancesPanel(currentUser);
        setLayout(new BorderLayout());
        add(fresh, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void clearForm() {
        typeCombo.setSelectedIndex(0);
        descriptionField.setText("");
        amountField.setText("");
        categoryCombo.setSelectedIndex(0);
        dateField.setText("");
        transactionsTable.clearSelection();
    }
}