package com.tukac.ui.panels;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class ManageBlogPanel extends JPanel {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable postsTable;

    private JTextField titleField;
    private JTextArea bodyField;
    private JComboBox<String> categoryCombo;

    public ManageBlogPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JLabel header = new JLabel("Manage Blog Posts");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setBorder(null);

        // ===== FORM =====
        JPanel formCard = ThemeManager.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel formTitle = new JLabel("Post Details");
        formTitle.setFont(ThemeManager.FONT_SUBHEADING);
        formTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createVerticalStrut(15));

        // Title
        addFormLabel(formCard, "Title");
        titleField = ThemeManager.createTextField(25);
        titleField.setMaximumSize(new Dimension(350, 36));
        titleField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(titleField);
        formCard.add(Box.createVerticalStrut(10));

        // Category
        addFormLabel(formCard, "Category");
        categoryCombo = new JComboBox<>(new String[]{
            "General", "Announcement", "Story", "Update", "Achievement", "Awareness", "Event Recap"
        });
        categoryCombo.setFont(ThemeManager.FONT_BODY);
        categoryCombo.setMaximumSize(new Dimension(350, 36));
        categoryCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(categoryCombo);
        formCard.add(Box.createVerticalStrut(10));

        // Body
        addFormLabel(formCard, "Content");
        bodyField = new JTextArea(10, 25);
        bodyField.setFont(ThemeManager.FONT_BODY);
        bodyField.setLineWrap(true);
        bodyField.setWrapStyleWord(true);
        JScrollPane bodyScroll = new JScrollPane(bodyField);
        bodyScroll.setMaximumSize(new Dimension(350, 200));
        bodyScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyScroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER, 1));
        formCard.add(bodyScroll);
        formCard.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnPanel.setBackground(ThemeManager.BG_CARD);
        btnPanel.setMaximumSize(new Dimension(350, 45));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addBtn = ThemeManager.createButton("Publish", ThemeManager.SUCCESS);
        addBtn.addActionListener(e -> addPost());
        btnPanel.add(addBtn);

        JButton updateBtn = ThemeManager.createButton("Update", ThemeManager.PRIMARY);
        updateBtn.addActionListener(e -> updatePost());
        btnPanel.add(updateBtn);

        JButton deleteBtn = ThemeManager.createButton("Delete", ThemeManager.DANGER);
        deleteBtn.addActionListener(e -> deletePost());
        btnPanel.add(deleteBtn);

        JButton clearBtn = ThemeManager.createButton("Clear", new Color(107, 114, 128));
        clearBtn.addActionListener(e -> clearForm());
        btnPanel.add(clearBtn);

        formCard.add(btnPanel);

        splitPane.setLeftComponent(formCard);

        // ===== TABLE =====
        String[] columns = {"ID", "Title", "Category", "Author", "Published"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        postsTable = new JTable(tableModel);
        postsTable.setRowHeight(32);
        postsTable.setFont(ThemeManager.FONT_BODY);
        postsTable.getTableHeader().setFont(ThemeManager.FONT_BUTTON);
        postsTable.setGridColor(ThemeManager.BORDER);
        postsTable.setSelectionBackground(new Color(219, 234, 254));
        postsTable.getColumnModel().getColumn(0).setMaxWidth(40);
        postsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Center align some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        postsTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        postsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        // Click row to populate form
        postsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = postsTable.getSelectedRow();
                if (row >= 0) {
                    int postId = (int) tableModel.getValueAt(row, 0);
                    populateForm(postId);
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(postsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER));
        splitPane.setRightComponent(tableScroll);

        add(splitPane, BorderLayout.CENTER);

        loadPosts();
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = ThemeManager.createLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(3));
    }

    private void loadPosts() {
        tableModel.setRowCount(0);
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("""
                SELECT bp.*, u.name as author_name
                FROM blog_posts bp
                LEFT JOIN users u ON bp.author_id = u.id
                ORDER BY bp.published_at DESC
            """);
            while (rs.next()) {
                String authorName = rs.getString("author_name");
                if (authorName == null) authorName = "Unknown";
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("category"),
                    authorName,
                    rs.getString("published_at")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void populateForm(int postId) {
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "SELECT * FROM blog_posts WHERE id = ?"
            );
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                titleField.setText(rs.getString("title"));
                bodyField.setText(rs.getString("body"));
                String cat = rs.getString("category");
                if (cat != null) categoryCombo.setSelectedItem(cat);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void addPost() {
        if (titleField.getText().trim().isEmpty() || bodyField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Content are required.");
            return;
        }
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "INSERT INTO blog_posts (title, body, category, author_id) VALUES (?, ?, ?, ?)"
            );
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setString(2, bodyField.getText().trim());
            pstmt.setString(3, (String) categoryCombo.getSelectedItem());
            pstmt.setInt(4, currentUser.getId());
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Post published!");
            clearForm();
            loadPosts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updatePost() {
        int row = postsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a post to update."); return; }
        int postId = (int) tableModel.getValueAt(row, 0);
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE blog_posts SET title=?, body=?, category=? WHERE id=?"
            );
            pstmt.setString(1, titleField.getText().trim());
            pstmt.setString(2, bodyField.getText().trim());
            pstmt.setString(3, (String) categoryCombo.getSelectedItem());
            pstmt.setInt(4, postId);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Post updated!");
            clearForm();
            loadPosts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deletePost() {
        int row = postsTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a post to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this post and all its comments?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int postId = (int) tableModel.getValueAt(row, 0);
        try {
            Statement stmt = Database.getConnection().createStatement();
            stmt.execute("DELETE FROM comments WHERE post_id = " + postId);
            stmt.execute("DELETE FROM blog_posts WHERE id = " + postId);
            JOptionPane.showMessageDialog(this, "Post deleted!");
            clearForm();
            loadPosts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void clearForm() {
        titleField.setText("");
        bodyField.setText("");
        categoryCombo.setSelectedIndex(0);
        postsTable.clearSelection();
    }
}