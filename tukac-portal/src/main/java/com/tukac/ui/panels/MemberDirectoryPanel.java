package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class MemberDirectoryPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable membersTable;
    private JTextField searchField;

    public MemberDirectoryPanel(User user) {
        setLayout(new BorderLayout(0, 15));
        setBackground(ThemeManager.BG_MAIN);

        // Header with search
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(ThemeManager.BG_MAIN);

        JLabel title = new JLabel("Member Directory");
        title.setFont(ThemeManager.FONT_HEADING);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(ThemeManager.BG_MAIN);

        searchField = ThemeManager.createTextField(18);
        searchField.setPreferredSize(new Dimension(200, 34));
        searchField.putClientProperty("JTextField.placeholderText", "Search members...");
        searchPanel.add(searchField);

        JButton searchBtn = ThemeManager.createButton("Search", ThemeManager.PRIMARY);
        searchBtn.addActionListener(e -> loadMembers(searchField.getText().trim()));
        searchPanel.add(searchBtn);

        JButton clearBtn = ThemeManager.createButton("Clear", new Color(107, 114, 128));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadMembers("");
        });
        searchPanel.add(clearBtn);

        header.add(searchPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Enter key to search
        searchField.addActionListener(e -> loadMembers(searchField.getText().trim()));

        // Table
        String[] columns = {"ID", "Name", "Student ID", "Email", "Role", "Contact", "Joined"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        membersTable = new JTable(tableModel);
        membersTable.setRowHeight(35);
        membersTable.setFont(ThemeManager.FONT_BODY);
        membersTable.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        membersTable.setGridColor(ThemeManager.BORDER);
        membersTable.setSelectionBackground(new Color(219, 234, 254));
        membersTable.getColumnModel().getColumn(0).setMaxWidth(40);
        membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color code role
        membersTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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

        JScrollPane scrollPane = new JScrollPane(membersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        add(scrollPane, BorderLayout.CENTER);

        // Stats bar
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        statsBar.setBackground(ThemeManager.BG_MAIN);
        add(statsBar, BorderLayout.SOUTH);

        loadMembers("");
    }

    private void loadMembers(String search) {
        tableModel.setRowCount(0);
        try {
            String sql;
            PreparedStatement pstmt;

            if (search.isEmpty()) {
                sql = "SELECT * FROM users WHERE is_approved = 1 ORDER BY name ASC";
                pstmt = Database.getConnection().prepareStatement(sql);
            } else {
                sql = "SELECT * FROM users WHERE is_approved = 1 AND (name LIKE ? OR student_id LIKE ? OR email LIKE ?) ORDER BY name ASC";
                pstmt = Database.getConnection().prepareStatement(sql);
                String wildcard = "%" + search + "%";
                pstmt.setString(1, wildcard);
                pstmt.setString(2, wildcard);
                pstmt.setString(3, wildcard);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String contact = rs.getString("contact");
                if (contact == null) contact = "—";
                String created = rs.getString("created_at");
                if (created != null && created.length() > 10) created = created.substring(0, 10);

                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("student_id"),
                    rs.getString("email"),
                    rs.getString("role").toUpperCase(),
                    contact,
                    created
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}