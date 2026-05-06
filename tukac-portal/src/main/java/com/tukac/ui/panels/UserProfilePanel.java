package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class UserProfilePanel extends JPanel {
    private User currentUser;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField contactField;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public UserProfilePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 20));
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JLabel header = new JLabel("My Profile");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        // Main content — two cards side by side
        JPanel cardsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        cardsPanel.setBackground(ThemeManager.BG_MAIN);

        // === LEFT: Profile Info ===
        JPanel profileCard = ThemeManager.createCard();
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));

        JLabel profileTitle = new JLabel("Profile Information");
        profileTitle.setFont(ThemeManager.FONT_SUBHEADING);
        profileTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        profileTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(profileTitle);
        profileCard.add(Box.createVerticalStrut(15));

        // Avatar
        JPanel avatarRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        avatarRow.setBackground(ThemeManager.BG_CARD);
        avatarRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        avatarRow.setMaximumSize(new Dimension(400, 50));

        String initials = "";
        String[] nameParts = user.getName().split(" ");
        for (String part : nameParts) {
            if (!part.isEmpty()) initials += part.charAt(0);
        }
        if (initials.length() > 2) initials = initials.substring(0, 2);

        JLabel avatar = new JLabel(initials.toUpperCase(), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(44, 44));
        avatar.setOpaque(true);
        avatar.setBackground(ThemeManager.PRIMARY);
        avatar.setForeground(ThemeManager.TEXT_WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avatar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        avatarRow.add(avatar);

        JPanel nameRolePanel = new JPanel();
        nameRolePanel.setLayout(new BoxLayout(nameRolePanel, BoxLayout.Y_AXIS));
        nameRolePanel.setBackground(ThemeManager.BG_CARD);
        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        nameRolePanel.add(nameLabel);
        JLabel roleLabel = new JLabel(user.getRole().toUpperCase() + "  •  " + user.getStudentId());
        roleLabel.setFont(ThemeManager.FONT_SMALL);
        roleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        nameRolePanel.add(roleLabel);
        avatarRow.add(nameRolePanel);

        profileCard.add(avatarRow);
        profileCard.add(Box.createVerticalStrut(20));

        // Editable fields
        addFormField(profileCard, "Full Name");
        nameField = ThemeManager.createTextField(25);
        nameField.setText(user.getName());
        nameField.setMaximumSize(new Dimension(400, 36));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(nameField);
        profileCard.add(Box.createVerticalStrut(12));

        addFormField(profileCard, "Email");
        emailField = ThemeManager.createTextField(25);
        emailField.setText(user.getEmail());
        emailField.setMaximumSize(new Dimension(400, 36));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(emailField);
        profileCard.add(Box.createVerticalStrut(12));

        addFormField(profileCard, "Contact Number");
        contactField = ThemeManager.createTextField(25);
        contactField.setMaximumSize(new Dimension(400, 36));
        contactField.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(contactField);
        profileCard.add(Box.createVerticalStrut(12));

        // Load contact from DB
        loadContact();

        // Read-only info
        addFormField(profileCard, "Student ID (read-only)");
        JTextField studentIdField = ThemeManager.createTextField(25);
        studentIdField.setText(user.getStudentId());
        studentIdField.setEditable(false);
        studentIdField.setBackground(ThemeManager.BG_MAIN);
        studentIdField.setMaximumSize(new Dimension(400, 36));
        studentIdField.setAlignmentX(Component.LEFT_ALIGNMENT);
        profileCard.add(studentIdField);
        profileCard.add(Box.createVerticalStrut(20));

        JButton saveBtn = ThemeManager.createButton("Save Changes", ThemeManager.SUCCESS);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveProfile());
        profileCard.add(saveBtn);

        cardsPanel.add(profileCard);

        // === RIGHT: Change Password ===
        JPanel passwordCard = ThemeManager.createCard();
        passwordCard.setLayout(new BoxLayout(passwordCard, BoxLayout.Y_AXIS));

        JLabel passTitle = new JLabel("Change Password");
        passTitle.setFont(ThemeManager.FONT_SUBHEADING);
        passTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        passTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordCard.add(passTitle);
        passwordCard.add(Box.createVerticalStrut(15));

        addFormField(passwordCard, "Current Password");
        oldPasswordField = ThemeManager.createPasswordField(25);
        oldPasswordField.setMaximumSize(new Dimension(400, 36));
        oldPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordCard.add(oldPasswordField);
        passwordCard.add(Box.createVerticalStrut(12));

        addFormField(passwordCard, "New Password");
        newPasswordField = ThemeManager.createPasswordField(25);
        newPasswordField.setMaximumSize(new Dimension(400, 36));
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordCard.add(newPasswordField);
        passwordCard.add(Box.createVerticalStrut(12));

        addFormField(passwordCard, "Confirm New Password");
        confirmPasswordField = ThemeManager.createPasswordField(25);
        confirmPasswordField.setMaximumSize(new Dimension(400, 36));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordCard.add(confirmPasswordField);
        passwordCard.add(Box.createVerticalStrut(20));

        JButton changePassBtn = ThemeManager.createButton("Update Password", ThemeManager.PRIMARY);
        changePassBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        changePassBtn.addActionListener(e -> changePassword());
        passwordCard.add(changePassBtn);

        passwordCard.add(Box.createVerticalGlue());
        cardsPanel.add(passwordCard);

        add(cardsPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, String labelText) {
        JLabel label = ThemeManager.createLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
    }

    private void loadContact() {
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "SELECT contact FROM users WHERE id = ?"
            );
            pstmt.setInt(1, currentUser.getId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String contact = rs.getString("contact");
                if (contact != null) contactField.setText(contact);
            }
        } catch (SQLException e) {
            System.err.println("Error loading contact: " + e.getMessage());
        }
    }

    private void saveProfile() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and email are required.");
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email.");
            return;
        }

        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE users SET name = ?, email = ?, contact = ? WHERE id = ?"
            );
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, contact.isEmpty() ? null : contact);
            pstmt.setInt(4, currentUser.getId());
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (Exception e) {
            if (e.getMessage().contains("UNIQUE")) {
                JOptionPane.showMessageDialog(this, "Email already in use by another account.");
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void changePassword() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All password fields are required.");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.");
            return;
        }
        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "New password must be at least 6 characters.");
            return;
        }

        try {
            // Verify old password
            PreparedStatement check = Database.getConnection().prepareStatement(
                "SELECT password FROM users WHERE id = ?"
            );
            check.setInt(1, currentUser.getId());
            ResultSet rs = check.executeQuery();
            if (rs.next() && !rs.getString("password").equals(oldPass)) {
                JOptionPane.showMessageDialog(this, "Current password is incorrect.");
                return;
            }

            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "UPDATE users SET password = ? WHERE id = ?"
            );
            pstmt.setString(1, newPass);
            pstmt.setInt(2, currentUser.getId());
            pstmt.executeUpdate();

            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            JOptionPane.showMessageDialog(this, "Password changed successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}