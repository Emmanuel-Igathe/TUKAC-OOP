package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class MyRsvpsPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable rsvpTable;

    public MyRsvpsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 15));
        setBackground(ThemeManager.BG_MAIN);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.BG_MAIN);

        JLabel title = new JLabel("My RSVPs");
        title.setFont(ThemeManager.FONT_HEADING);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"Event ID", "Title", "Date", "Time", "Location", "RSVP Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        rsvpTable = new JTable(tableModel);
        rsvpTable.setRowHeight(35);
        rsvpTable.setFont(ThemeManager.FONT_BODY);
        rsvpTable.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        rsvpTable.setGridColor(ThemeManager.BORDER);
        rsvpTable.setSelectionBackground(new Color(219, 234, 254));
        rsvpTable.getColumnModel().getColumn(0).setMaxWidth(60);
        rsvpTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        rsvpTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        rsvpTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        rsvpTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(rsvpTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBackground(ThemeManager.BG_MAIN);

        JButton cancelBtn = ThemeManager.createButton("Cancel Selected RSVP", ThemeManager.DANGER);
        cancelBtn.addActionListener(e -> cancelRsvp());
        bottomPanel.add(cancelBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        loadRsvps();
    }

    private void loadRsvps() {
        tableModel.setRowCount(0);
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement("""
                SELECT e.id, e.title, e.event_date, e.event_time, e.location, er.registered_at
                FROM event_registrations er
                JOIN events e ON er.event_id = e.id
                WHERE er.user_id = ?
                ORDER BY e.event_date ASC
            """);
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String regDate = rs.getString("registered_at");
                if (regDate != null && regDate.length() > 10) regDate = regDate.substring(0, 10);
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("location"),
                    regDate
                });
            }

            if (tableModel.getRowCount() == 0) {
                // Show empty state message
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void cancelRsvp() {
        int row = rsvpTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an RSVP to cancel.");
            return;
        }

        int eventId = (int) tableModel.getValueAt(row, 0);
        String eventTitle = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel your RSVP for \"" + eventTitle + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "DELETE FROM event_registrations WHERE user_id = ? AND event_id = ?"
            );
            pstmt.setInt(1, currentUser.getId());
            pstmt.setInt(2, eventId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "RSVP cancelled.");
            loadRsvps();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}