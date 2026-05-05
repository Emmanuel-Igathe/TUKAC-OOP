package com.tukac.ui.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.tukac.db.Database;
import com.tukac.ui.ThemeManager;

public class RegisterPanel extends JPanel {
    private JTextField nameField;
    private JTextField studentIdField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel messageLabel;

    public RegisterPanel(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.BG_MAIN);

        // Center card
        JPanel card = ThemeManager.createCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 560));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 20, 4, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        // Back button
        JButton backBtn = ThemeManager.createLinkButton("\u2190 Back to Home");
        backBtn.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new WelcomeScreen(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(backBtn, gbc);

        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.PRIMARY);
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        card.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Join the TUK Ability Club", SwingConstants.CENTER);
        subtitleLabel.setFont(ThemeManager.FONT_BODY);
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        gbc.gridy = 2;
        card.add(subtitleLabel, gbc);

        gbc.gridy = 3;
        card.add(Box.createVerticalStrut(8), gbc);

        // Full Name
        gbc.gridy = 4;
        card.add(ThemeManager.createLabel("Full Name"), gbc);
        nameField = ThemeManager.createTextField(20);
        gbc.gridy = 5;
        card.add(nameField, gbc);

        // Student ID
        gbc.gridy = 6;
        card.add(ThemeManager.createLabel("Student ID"), gbc);
        studentIdField = ThemeManager.createTextField(20);
        gbc.gridy = 7;
        card.add(studentIdField, gbc);

        // Email
        gbc.gridy = 8;
        card.add(ThemeManager.createLabel("Email"), gbc);
        emailField = ThemeManager.createTextField(20);
        gbc.gridy = 9;
        card.add(emailField, gbc);

        // Password
        gbc.gridy = 10;
        card.add(ThemeManager.createLabel("Password"), gbc);
        passwordField = ThemeManager.createPasswordField(20);
        gbc.gridy = 11;
        card.add(passwordField, gbc);

        // Confirm Password
        gbc.gridy = 12;
        card.add(ThemeManager.createLabel("Confirm Password"), gbc);
        confirmPasswordField = ThemeManager.createPasswordField(20);
        gbc.gridy = 13;
        card.add(confirmPasswordField, gbc);

        gbc.gridy = 14;
        card.add(Box.createVerticalStrut(8), gbc);

        // Register button
        JButton registerButton = ThemeManager.createButton("Create Account", ThemeManager.SUCCESS);
        registerButton.setPreferredSize(new Dimension(250, 40));
        gbc.gridy = 15;
        card.add(registerButton, gbc);

        // Back to login
        JButton loginButton = ThemeManager.createLinkButton("Already have an account? Sign in");
        gbc.gridy = 16;
        card.add(loginButton, gbc);

        // Message label
        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(ThemeManager.FONT_SMALL);
        messageLabel.setForeground(ThemeManager.DANGER);
        gbc.gridy = 17;
        card.add(messageLabel, gbc);

        add(card);

        // Actions
        registerButton.addActionListener(e -> handleRegister());
        loginButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new LoginPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || studentId.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(ThemeManager.DANGER);
            messageLabel.setText("All fields are required.");
            return;
        }
        if (!email.contains("@")) {
            messageLabel.setForeground(ThemeManager.DANGER);
            messageLabel.setText("Please enter a valid email.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setForeground(ThemeManager.DANGER);
            messageLabel.setText("Passwords do not match.");
            return;
        }
        if (password.length() < 6) {
            messageLabel.setForeground(ThemeManager.DANGER);
            messageLabel.setText("Password must be at least 6 characters.");
            return;
        }

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users (name, student_id, email, password) VALUES (?, ?, ?, ?)"
            );
            pstmt.setString(1, name);
            pstmt.setString(2, studentId);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.executeUpdate();

            messageLabel.setForeground(ThemeManager.SUCCESS);
            messageLabel.setText("Registered successfully! Awaiting admin approval.");

        } catch (Exception ex) {
            if (ex.getMessage().contains("UNIQUE")) {
                messageLabel.setText("Student ID or email already exists.");
            } else {
                messageLabel.setText("Error: " + ex.getMessage());
            }
        }
    }
}