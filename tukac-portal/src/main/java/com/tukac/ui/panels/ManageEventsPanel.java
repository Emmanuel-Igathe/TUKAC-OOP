package com.tukac.ui.panels;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManageEventsPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable eventsTable;

    private JTextField titleField;
    private JTextArea descriptionField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField locationField;
    private JTextField capacityField;

    public ManageEventsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JLabel header = new JLabel("Manage Events");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(340);
        splitPane.setBorder(null);

        // ===== FORM =====
        JPanel formCard = ThemeManager.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Event Details");
        formTitle.setFont(ThemeManager.FONT_SUBHEADING);
        formTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(15));

        addFormField(formCard, "Title");
        titleField = ThemeManager.createTextField(20);
        titleField.setMaximumSize(new Dimension(300, 36));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(titleField);
        formCard.add(Box.createVerticalStrut(10));

        addFormField(formCard, "Description");
        descriptionField = new JTextArea(3, 20);
        descriptionField.setFont(ThemeManager.FONT_BODY);
        descriptionField.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionField);
        descScroll.setMaximumSize(new Dimension(300, 80));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(descScroll);
        formCard.add(Box.createVerticalStrut(10));

        addFormField(formCard, "Date (YYYY-MM-DD)");
        dateField = ThemeManager.createTextField(20);
        dateField.setMaximumSize(new Dimension(300, 36));
        dateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(dateField);
        formCard.add(Box.createVerticalStrut(10));

        addFormField(formCard, "Time (HH:MM)");
        timeField = ThemeManager.createTextField(20);
        timeField.setMaximumSize(new Dimension(300, 36));
        timeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(timeField);
        formCard.add(Box.createVerticalStrut(10));

        addFormField(formCard, "Location");
        locationField = ThemeManager.createTextField(20);
        locationField.setMaximumSize(new Dimension(300, 36));
        locationField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(locationField);
        formCard.add(Box.createVerticalStrut(10));

        addFormField(formCard, "Capacity");
        capacityField = ThemeManager.createTextField(20);
        capacityField.setMaximumSize(new Dimension(300, 36));
        capacityField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(capacityField);
        formCard.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(ThemeManager.BG_CARD);
        btnPanel.setMaximumSize(new Dimension(300, 45));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addBtn = ThemeManager.createButton("Add", ThemeManager.SUCCESS);
        addBtn.addActionListener(e -> addEvent());
        btnPanel.add(addBtn);

        JButton updateBtn = ThemeManager.createButton("Update", ThemeManager.PRIMARY);
        updateBtn.addActionListener(e -> updateEvent());
        btnPanel.add(updateBtn);

        JButton deleteBtn = ThemeManager.createButton("Delete", ThemeManager.DANGER);
        deleteBtn.addActionListener(e -> deleteEvent());
        btnPanel.add(deleteBtn);

        JButton clearBtn = ThemeManager.createButton("Clear", new Color(107, 114, 128));
        clearBtn.addActionListener(e -> clearForm());
        btnPanel.add(clearBtn);

        formCard.add(btnPanel);

        splitPane.setLeftComponent(formCard);

        // ===== TABLE =====
        String[] columns = {"ID", "Title", "Date", "Time", "Location", "Capacity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        eventsTable = new JTable(tableModel);
        eventsTable.setRowHeight(32);
        eventsTable.setFont(ThemeManager.FONT_BODY);
        eventsTable.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        eventsTable.setGridColor(ThemeManager.BORDER);
        eventsTable.setSelectionBackground(new Color(219, 234, 254));
        eventsTable.getColumnModel().getColumn(0).setMaxWidth(40);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        eventsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = eventsTable.getSelectedRow();
                if (row >= 0) {
                    titleField.setText((String) tableModel.getValueAt(row, 1));
                    dateField.setText((String) tableModel.getValueAt(row, 2));
                    timeField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                    locationField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
                    capacityField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(eventsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        splitPane.setRightComponent(tableScroll);

        add(splitPane, BorderLayout.CENTER);

        loadEvents();
    }

    private void addFormField(JPanel panel, String labelText) {
        JLabel label = ThemeManager.createLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
    }

    private void loadEvents() {
        tableModel.setRowCount(0);
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM events ORDER BY event_date ASC");
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("location"),
                    rs.getInt("capacity")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void addEvent() {
        if (titleField.getText().trim().isEmpty() || dateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Date are required.");
            return;
        }
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "INSERT INTO events (title, description, event_date, event_time, location, capacity, created_by) VALUES (?,?,?,?,?,?,?)"
            );
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setString(2, descriptionField.getText().trim());
            pstmt.setString(3, dateField.getText().trim());
            pstmt.setString(4, timeField.getText().trim());
            pstmt.setString(5, locationField.getText().trim());
            pstmt.setInt(6, Integer.parseInt(capacityField.getText().trim().isEmpty() ? "0" : capacityField.getText().trim()));
            pstmt.setInt(7, currentUser.getId());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event added!");
            clearForm();
            loadEvents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateEvent() {
        int row = eventsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an event to update."); return; }
        int eventId = (int) tableModel.getValueAt(row, 0);
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE events SET title=?, description=?, event_date=?, event_time=?, location=?, capacity=? WHERE id=?"
            );
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setString(2, descriptionField.getText().trim());
            pstmt.setString(3, dateField.getText().trim());
            pstmt.setString(4, timeField.getText().trim());
            pstmt.setString(5, locationField.getText().trim());
            pstmt.setInt(6, Integer.parseInt(capacityField.getText().trim().isEmpty() ? "0" : capacityField.getText().trim()));
            pstmt.setInt(7, eventId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event updated!");
            clearForm();
            loadEvents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteEvent() {
        int row = eventsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an event to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this event?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int eventId = (int) tableModel.getValueAt(row, 0);
        try {
            Statement stmt = Database.getConnection().createStatement();
            stmt.execute("DELETE FROM event_registrations WHERE event_id = " + eventId);
            stmt.execute("DELETE FROM events WHERE id = " + eventId);
            JOptionPane.showMessageDialog(this, "Event deleted!");
            clearForm();
            loadEvents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearForm() {
        titleField.setText("");
        descriptionField.setText("");
        dateField.setText("");
        timeField.setText("");
        locationField.setText("");
        capacityField.setText("");
        eventsTable.clearSelection();
    }
}