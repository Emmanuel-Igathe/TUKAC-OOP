package com.tukac.ui.panels;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class ManageUsersPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable usersTable;
    private JComboBox<String> roleCombo;
    private JLabel pendingLabel;

    public ManageUsersPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 15));
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.BG_MAIN);

        JLabel title = new JLabel("Manage Users");
        title.setFont(ThemeManager.FONT_HEADING);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Center
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setBackground(ThemeManager.BG_MAIN);

        // Pending alert
        pendingLabel = new JLabel();
        pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pendingLabel.setForeground(new Color(146, 64, 14));
        pendingLabel.setOpaque(true);
        pendingLabel.setBackground(new Color(254, 243, 199));
        pendingLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(234, 179, 8), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        centerPanel.add(pendingLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Student ID", "Email", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        usersTable = new JTable(tableModel);
        usersTable.setRowHeight(35);
        usersTable.setFont(ThemeManager.FONT_BODY);
        usersTable.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        usersTable.setGridColor(ThemeManager.BORDER);
        usersTable.setSelectionBackground(new Color(219, 234, 254));
        usersTable.getColumnModel().getColumn(0).setMaxWidth(40);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color code status
        usersTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    String status = (String) value;
                    if ("Approved".equals(status)) {
                        setForeground(ThemeManager.SUCCESS);
                    } else {
                        setForeground(new Color(234, 179, 8));
                    }
                }
                return c;
            }
        });

        // Color code role
        usersTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    String role = (String) value;
                    switch (role) {
                        case "ADMIN" -> setForeground(ThemeManager.DANGER);
                        case "EXECUTIVE" -> setForeground(ThemeManager.PURPLE);
                        default -> setForeground(ThemeManager.PRIMARY);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom action panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        bottomPanel.setBackground(ThemeManager.BG_MAIN);

        JButton approveBtn = ThemeManager.createButton("Approve", ThemeManager.SUCCESS);
        approveBtn.addActionListener(e -> setApproval(1));
        bottomPanel.add(approveBtn);

        JButton rejectBtn = ThemeManager.createButton("Reject", ThemeManager.DANGER);
        rejectBtn.addActionListener(e -> setApproval(0));
        bottomPanel.add(rejectBtn);

        // Separator
        bottomPanel.add(new JLabel("  |  "));

        bottomPanel.add(ThemeManager.createLabel("Change Role:"));
        roleCombo = new JComboBox<>(new String[]{"member", "executive", "admin"});
        roleCombo.setFont(ThemeManager.FONT_BODY);
        roleCombo.setPreferredSize(new Dimension(120, 34));
        bottomPanel.add(roleCombo);

        JButton changeRoleBtn = ThemeManager.createButton("Apply Role", ThemeManager.PURPLE);
        changeRoleBtn.addActionListener(e -> changeRole());
        bottomPanel.add(changeRoleBtn);

        bottomPanel.add(new JLabel("  |  "));

        JButton deleteBtn = ThemeManager.createButton("Delete User", ThemeManager.DANGER);
        deleteBtn.addActionListener(e -> deleteUser());
        bottomPanel.add(deleteBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        loadUsers();
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        int pendingCount = 0;
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY is_approved ASC, created_at DESC");
            while (rs.next()) {
                int approved = rs.getInt("is_approved");
                if (approved == 0) pendingCount++;
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("student_id"),
                    rs.getString("email"),
                    rs.getString("role").toUpperCase(),
                    approved == 1 ? "Approved" : "Pending"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        if (pendingCount > 0) {
            pendingLabel.setText("  \u26A0  " + pendingCount + " user(s) pending approval");
            pendingLabel.setVisible(true);
        } else {
            pendingLabel.setText("  \u2714  All users are approved");
            pendingLabel.setForeground(new Color(22, 101, 52));
            pendingLabel.setBackground(new Color(220, 252, 231));
            pendingLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.SUCCESS, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        }
    }

    private int getSelectedUserId() {
        int row = usersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user first.");
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void setApproval(int approved) {
        int userId = getSelectedUserId();
        if (userId == -1) return;

        String action = approved == 1 ? "approve" : "reject";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to " + action + " this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE users SET is_approved = ? WHERE id = ?"
            );
            pstmt.setInt(1, approved);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User " + action + "d successfully!");
            loadUsers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void changeRole() {
        int userId = getSelectedUserId();
        if (userId == -1) return;

        if (userId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "You cannot change your own role.");
            return;
        }

        String newRole = (String) roleCombo.getSelectedItem();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Change this user's role to " + newRole.toUpperCase() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE users SET role = ? WHERE id = ?"
            );
            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Role updated to " + newRole.toUpperCase() + "!");
            loadUsers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteUser() {
        int userId = getSelectedUserId();
        if (userId == -1) return;

        if (userId == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Permanently delete this user and all their data?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Statement stmt = Database.getConnection().createStatement();
            stmt.execute("DELETE FROM comments WHERE user_id = " + userId);
            stmt.execute("DELETE FROM event_registrations WHERE user_id = " + userId);
            stmt.execute("DELETE FROM users WHERE id = " + userId);
            JOptionPane.showMessageDialog(this, "User deleted.");
            loadUsers();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}