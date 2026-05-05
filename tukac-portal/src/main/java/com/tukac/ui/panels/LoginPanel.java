package com.tukac.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

public class LoginPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginPanel(JFrame parentFrame) {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.BG_MAIN);

        JPanel card = ThemeManager.createCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 460));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton backBtn = ThemeManager.createLinkButton("\u2190 Back to Home");
        backBtn.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new WelcomeScreen(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        card.add(backBtn, gbc);

        JLabel titleLabel = new JLabel("TUK Ability Club", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.PRIMARY);
        gbc.gridy = 1; gbc.anchor = GridBagConstraints.CENTER;
        card.add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(ThemeManager.FONT_BODY);
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        gbc.gridy = 2;
        card.add(subtitleLabel, gbc);

        gbc.gridy = 3; card.add(Box.createVerticalStrut(10), gbc);

        gbc.gridy = 4;
        card.add(ThemeManager.createLabel("Email or Student ID"), gbc);
        emailField = ThemeManager.createTextField(20);
        gbc.gridy = 5; card.add(emailField, gbc);

        gbc.gridy = 6;
        card.add(ThemeManager.createLabel("Password"), gbc);
        passwordField = ThemeManager.createPasswordField(20);
        gbc.gridy = 7; card.add(passwordField, gbc);

        gbc.gridy = 8; card.add(Box.createVerticalStrut(8), gbc);

        JButton loginButton = ThemeManager.createButton("Sign In", ThemeManager.PRIMARY);
        loginButton.setPreferredSize(new Dimension(250, 40));
        gbc.gridy = 9; card.add(loginButton, gbc);

        JButton registerButton = ThemeManager.createLinkButton("Don't have an account? Register here");
        gbc.gridy = 10; card.add(registerButton, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(ThemeManager.FONT_SMALL);
        messageLabel.setForeground(ThemeManager.DANGER);
        gbc.gridy = 11; card.add(messageLabel, gbc);

        add(card);

        loginButton.addActionListener(e -> handleLogin(parentFrame));
        registerButton.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new RegisterPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        passwordField.addActionListener(e -> handleLogin(parentFrame));
    }

    private void handleLogin(JFrame parentFrame) {
        String emailOrId = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (emailOrId.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(ThemeManager.DANGER);
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * FROM users WHERE (email = ? OR student_id = ?) AND password = ?"
            );
            pstmt.setString(1, emailOrId);
            pstmt.setString(2, emailOrId);
            pstmt.setString(3, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int isApproved = rs.getInt("is_approved");
                if (isApproved == 0) {
                    messageLabel.setForeground(new Color(234, 179, 8));
                    messageLabel.setText("Account pending admin approval.");
                    return;
                }

                String userName = rs.getString("name");
                String role = rs.getString("role");
                int userId = rs.getInt("id");

                com.tukac.models.User user = new com.tukac.models.User(
                    userId, userName, emailOrId, rs.getString("email"), role
                );

                parentFrame.getContentPane().removeAll();

                // ROUTE BY ROLE — Members get their own interface
                if (user.isMember()) {
                    parentFrame.getContentPane().add(new MemberHomePanel(parentFrame, user));
                } else {
                    // Executives and Admins get the dashboard with sidebar
                    parentFrame.getContentPane().add(new DashboardPanel(parentFrame, user));
                }

                parentFrame.revalidate();
                parentFrame.repaint();

            } else {
                messageLabel.setForeground(ThemeManager.DANGER);
                messageLabel.setText("Invalid credentials. Try again.");
            }
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }
}