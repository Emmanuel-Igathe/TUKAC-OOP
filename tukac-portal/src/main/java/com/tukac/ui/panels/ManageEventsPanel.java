package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

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
        setLayout(new BorderLayout(10, 10));
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JLabel header = new JLabel("Manage Events");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        // Split: form left, table right
        JPanel mainPanel = new JPanel(new BorderLayout(12, 0));
        mainPanel.setBackground(ThemeManager.BG_MAIN);

        // ===== FORM (scroll) =====
        JPanel formCard = ThemeManager.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Event Details");
        formTitle.setFont(ThemeManager.FONT_SUBHEADING);
        formTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(12));

        addFormLabel(formCard, "Title *");
        titleField = ThemeManager.createTextField(20);
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(titleField);
        formCard.add(Box.createVerticalStrut(8));

        addFormLabel(formCard, "Description");
        descriptionField = new JTextArea(3, 20);
        descriptionField.setFont(ThemeManager.FONT_BODY);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionField);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(descScroll);
        formCard.add(Box.createVerticalStrut(8));

        addFormLabel(formCard, "Date * (YYYY-MM-DD)");
        dateField = ThemeManager.createTextField(20);
        dateField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        dateField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(dateField);
        formCard.add(Box.createVerticalStrut(8));

        addFormLabel(formCard, "Time (HH:MM)");
        timeField = ThemeManager.createTextField(20);
        timeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        timeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(timeField);
        formCard.add(Box.createVerticalStrut(8));

        addFormLabel(formCard, "Location");
        locationField = ThemeManager.createTextField(20);
        locationField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        locationField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(locationField);
        formCard.add(Box.createVerticalStrut(8));

        addFormLabel(formCard, "Capacity");
        capacityField = ThemeManager.createTextField(20);
        capacityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        capacityField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(capacityField);
        formCard.add(Box.createVerticalStrut(15));

        // Buttons — two rows for breathing room
        JPanel btnRow1 = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow1.setBackground(ThemeManager.BG_CARD);
        btnRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRow1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addBtn = ThemeManager.createButton("  Add Event  ", ThemeManager.SUCCESS);
        addBtn.setPreferredSize(new Dimension(0, 38));
        addBtn.addActionListener(e -> addEvent());
        btnRow1.add(addBtn);

        JButton updateBtn = ThemeManager.createButton("  Update  ", ThemeManager.PRIMARY);
        updateBtn.setPreferredSize(new Dimension(0, 38));
        updateBtn.addActionListener(e -> updateEvent());
        btnRow1.add(updateBtn);

        formCard.add(btnRow1);
        formCard.add(Box.createVerticalStrut(8));

        JPanel btnRow2 = new JPanel(new GridLayout(1, 2, 8, 0));
        btnRow2.setBackground(ThemeManager.BG_CARD);
        btnRow2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnRow2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton deleteBtn = ThemeManager.createButton("  Delete  ", ThemeManager.DANGER);
        deleteBtn.setPreferredSize(new Dimension(0, 38));
        deleteBtn.addActionListener(e -> deleteEvent());
        btnRow2.add(deleteBtn);

        JButton clearBtn = ThemeManager.createButton("  Clear  ", new Color(107, 114, 128));
        clearBtn.setPreferredSize(new Dimension(0, 38));
        clearBtn.addActionListener(e -> clearForm());
        btnRow2.add(clearBtn);

        formCard.add(btnRow2);

        JScrollPane formScroll = new JScrollPane(formCard);
        formScroll.setPreferredSize(new Dimension(320, 0));
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(formScroll, BorderLayout.WEST);

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
                    titleField.setText(str(tableModel.getValueAt(row, 1)));
                    dateField.setText(str(tableModel.getValueAt(row, 2)));
                    timeField.setText(str(tableModel.getValueAt(row, 3)));
                    locationField.setText(str(tableModel.getValueAt(row, 4)));
                    capacityField.setText(str(tableModel.getValueAt(row, 5)));
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(eventsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        mainPanel.add(tableScroll, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        loadEvents();
    }

    private String str(Object val) { return val == null ? "" : String.valueOf(val); }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = ThemeManager.createLabel(text);
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
                    rs.getInt("id"), rs.getString("title"), rs.getString("event_date"),
                    rs.getString("event_time"), rs.getString("location"), rs.getInt("capacity")
                });
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void addEvent() {
        if (titleField.getText().trim().isEmpty() || dateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Date are required."); return;
        }
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "INSERT INTO events (title, description, event_date, event_time, location, capacity, created_by) VALUES (?,?,?,?,?,?,?)");
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setString(2, descriptionField.getText().trim());
            pstmt.setString(3, dateField.getText().trim());
            pstmt.setString(4, timeField.getText().trim());
            pstmt.setString(5, locationField.getText().trim());
            pstmt.setInt(6, Integer.parseInt(capacityField.getText().trim().isEmpty() ? "0" : capacityField.getText().trim()));
            pstmt.setInt(7, currentUser.getId());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event added!"); clearForm(); loadEvents();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void updateEvent() {
        int row = eventsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an event to update."); return; }
        int eventId = (int) tableModel.getValueAt(row, 0);
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE events SET title=?, description=?, event_date=?, event_time=?, location=?, capacity=? WHERE id=?");
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setString(2, descriptionField.getText().trim());
            pstmt.setString(3, dateField.getText().trim());
            pstmt.setString(4, timeField.getText().trim());
            pstmt.setString(5, locationField.getText().trim());
            pstmt.setInt(6, Integer.parseInt(capacityField.getText().trim().isEmpty() ? "0" : capacityField.getText().trim()));
            pstmt.setInt(7, eventId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event updated!"); clearForm(); loadEvents();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void deleteEvent() {
        int row = eventsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an event to delete."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this event?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        int eventId = (int) tableModel.getValueAt(row, 0);
        try {
            Statement stmt = Database.getConnection().createStatement();
            stmt.execute("DELETE FROM event_registrations WHERE event_id = " + eventId);
            stmt.execute("DELETE FROM events WHERE id = " + eventId);
            JOptionPane.showMessageDialog(this, "Event deleted!"); clearForm(); loadEvents();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void clearForm() {
        titleField.setText(""); descriptionField.setText(""); dateField.setText("");
        timeField.setText(""); locationField.setText(""); capacityField.setText("");
        eventsTable.clearSelection();
    }
}