package com.tukac.ui.panels;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class EventsPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable eventsTable;

    public EventsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.BG_MAIN);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Upcoming Events");
        title.setFont(ThemeManager.FONT_HEADING);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Date", "Time", "Location", "Capacity", "RSVPs"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        eventsTable = new JTable(tableModel);
        eventsTable.setRowHeight(35);
        eventsTable.setFont(ThemeManager.FONT_BODY);
        eventsTable.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        eventsTable.getTableHeader().setBackground(ThemeManager.BG_CARD);
        eventsTable.setGridColor(ThemeManager.BORDER);
        eventsTable.setSelectionBackground(new Color(219, 234, 254));
        eventsTable.getColumnModel().getColumn(0).setMaxWidth(40);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Center align some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        eventsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        eventsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        eventsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        eventsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        bottomPanel.setBackground(ThemeManager.BG_MAIN);

        JButton rsvpButton = ThemeManager.createButton("RSVP to Event", ThemeManager.PRIMARY);
        rsvpButton.addActionListener(e -> handleRSVP());
        bottomPanel.add(rsvpButton);

        JButton cancelRsvpButton = ThemeManager.createButton("Cancel RSVP", ThemeManager.DANGER);
        cancelRsvpButton.addActionListener(e -> handleCancelRSVP());
        bottomPanel.add(cancelRsvpButton);

        add(bottomPanel, BorderLayout.SOUTH);

        loadEvents();
    }

    private void loadEvents() {
        tableModel.setRowCount(0);
        try {
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("""
                SELECT e.*,
                    (SELECT COUNT(*) FROM event_registrations WHERE event_id = e.id) as rsvp_count
                FROM events e
                ORDER BY e.event_date ASC
            """);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("location"),
                    rs.getInt("capacity"),
                    rs.getInt("rsvp_count")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + e.getMessage());
        }
    }

    private void handleRSVP() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event first.");
            return;
        }
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "INSERT OR IGNORE INTO event_registrations (user_id, event_id) VALUES (?, ?)"
            );
            pstmt.setInt(1, currentUser.getId());
            pstmt.setInt(2, eventId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "RSVP successful!");
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, "You have already RSVP'd to this event.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void handleCancelRSVP() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event first.");
            return;
        }
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "DELETE FROM event_registrations WHERE user_id = ? AND event_id = ?"
            );
            pstmt.setInt(1, currentUser.getId());
            pstmt.setInt(2, eventId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "RSVP cancelled.");
                loadEvents();
            } else {
                JOptionPane.showMessageDialog(this, "You haven't RSVP'd to this event.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}