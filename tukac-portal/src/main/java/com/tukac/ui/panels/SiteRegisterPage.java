package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.PreparedStatement;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.tukac.db.Database;
import com.tukac.ui.ThemeManager;

public class SiteRegisterPage extends JPanel {
    private JTextField nameField, studentIdField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JLabel messageLabel;
    private MainFrame mainFrame;

    public SiteRegisterPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ThemeManager.BG_MAIN);

        JPanel card = ThemeManager.createCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 20, 3, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; card.add(title, gbc);

        JLabel sub = new JLabel("Join the TUK Ability Club", SwingConstants.CENTER);
        sub.setFont(ThemeManager.FONT_BODY);
        sub.setForeground(ThemeManager.TEXT_SECONDARY);
        gbc.gridy = 1; card.add(sub, gbc);

        gbc.gridy = 2; card.add(Box.createVerticalStrut(6), gbc);

        gbc.gridy = 3; card.add(ThemeManager.createLabel("Full Name"), gbc);
        nameField = ThemeManager.createTextField(20); gbc.gridy = 4; card.add(nameField, gbc);

        gbc.gridy = 5; card.add(ThemeManager.createLabel("Student ID"), gbc);
        studentIdField = ThemeManager.createTextField(20); gbc.gridy = 6; card.add(studentIdField, gbc);

        gbc.gridy = 7; card.add(ThemeManager.createLabel("Email"), gbc);
        emailField = ThemeManager.createTextField(20); gbc.gridy = 8; card.add(emailField, gbc);

        gbc.gridy = 9; card.add(ThemeManager.createLabel("Password"), gbc);
        passwordField = ThemeManager.createPasswordField(20); gbc.gridy = 10; card.add(passwordField, gbc);

        gbc.gridy = 11; card.add(ThemeManager.createLabel("Confirm Password"), gbc);
        confirmPasswordField = ThemeManager.createPasswordField(20); gbc.gridy = 12; card.add(confirmPasswordField, gbc);

        gbc.gridy = 13; card.add(Box.createVerticalStrut(6), gbc);

        JButton regBtn = ThemeManager.createButton("Create Account", ThemeManager.SUCCESS);
        regBtn.setPreferredSize(new Dimension(250, 40));
        gbc.gridy = 14; card.add(regBtn, gbc);

        JButton loginLink = ThemeManager.createLinkButton("Already have an account? Sign in");
        gbc.gridy = 15; card.add(loginLink, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(ThemeManager.FONT_SMALL);
        messageLabel.setForeground(ThemeManager.DANGER);
        gbc.gridy = 16; card.add(messageLabel, gbc);

        center.add(card);
        add(center, BorderLayout.CENTER);
        add(MainFrame.createFooter(), BorderLayout.SOUTH);

        regBtn.addActionListener(e -> handleRegister());
        loginLink.addActionListener(e -> mainFrame.navigateTo("Login"));
    }

    private void handleRegister() {
        String name = nameField.getText().trim(), sid = studentIdField.getText().trim();
        String email = emailField.getText().trim(), pass = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || sid.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            msg("All fields are required.", ThemeManager.DANGER); return; }
        if (!email.contains("@")) { msg("Please enter a valid email.", ThemeManager.DANGER); return; }
        if (!pass.equals(confirm)) { msg("Passwords do not match.", ThemeManager.DANGER); return; }
        if (pass.length() < 6) { msg("Password must be at least 6 characters.", ThemeManager.DANGER); return; }

        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "INSERT INTO users (name, student_id, email, password) VALUES (?,?,?,?)");
            pstmt.setString(1, name); pstmt.setString(2, sid); pstmt.setString(3, email); pstmt.setString(4, pass);
            pstmt.executeUpdate();
            msg("Registered successfully! Awaiting admin approval.", ThemeManager.SUCCESS);
        } catch (Exception ex) {
            if (ex.getMessage().contains("UNIQUE")) msg("Student ID or email already exists.", ThemeManager.DANGER);
            else msg("Error: " + ex.getMessage(), ThemeManager.DANGER);
        }
    }

    private void msg(String text, Color color) { messageLabel.setForeground(color); messageLabel.setText(text); }
}