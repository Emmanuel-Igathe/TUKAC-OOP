package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.tukac.db.Database;
import com.tukac.ui.ThemeManager;

public class SiteLoginPage extends JPanel {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;
    private MainFrame mainFrame;

    public SiteLoginPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(ThemeManager.BG_MAIN);

        JPanel card = ThemeManager.createCard();
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(420, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 20, 6, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("TUK Ability Club", SwingConstants.CENTER);
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(title, gbc);

        JLabel sub = new JLabel("Sign in to your account", SwingConstants.CENTER);
        sub.setFont(ThemeManager.FONT_BODY);
        sub.setForeground(ThemeManager.TEXT_SECONDARY);
        gbc.gridy = 1; card.add(sub, gbc);

        gbc.gridy = 2; card.add(Box.createVerticalStrut(10), gbc);

        gbc.gridy = 3; card.add(ThemeManager.createLabel("Email or Student ID"), gbc);
        emailField = ThemeManager.createTextField(20);
        gbc.gridy = 4; card.add(emailField, gbc);

        gbc.gridy = 5; card.add(ThemeManager.createLabel("Password"), gbc);
        passwordField = ThemeManager.createPasswordField(20);
        gbc.gridy = 6; card.add(passwordField, gbc);

        gbc.gridy = 7; card.add(Box.createVerticalStrut(8), gbc);

        JButton loginBtn = ThemeManager.createButton("Sign In", ThemeManager.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(250, 40));
        gbc.gridy = 8; card.add(loginBtn, gbc);

        JButton regLink = ThemeManager.createLinkButton("Don't have an account? Register here");
        gbc.gridy = 9; card.add(regLink, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(ThemeManager.FONT_SMALL);
        messageLabel.setForeground(ThemeManager.DANGER);
        gbc.gridy = 10; card.add(messageLabel, gbc);

        center.add(card);
        add(center, BorderLayout.CENTER);
        add(MainFrame.createFooter(), BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
        regLink.addActionListener(e -> mainFrame.navigateTo("Register"));
    }

    private void handleLogin() {
        String emailOrId = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (emailOrId.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(ThemeManager.DANGER);
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement(
                "SELECT * FROM users WHERE (email = ? OR student_id = ?) AND password = ?");
            pstmt.setString(1, emailOrId);
            pstmt.setString(2, emailOrId);
            pstmt.setString(3, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (rs.getInt("is_approved") == 0) {
                    messageLabel.setForeground(new Color(234, 179, 8));
                    messageLabel.setText("Account pending admin approval.");
                    return;
                }
                com.tukac.models.User user = new com.tukac.models.User(
                    rs.getInt("id"), rs.getString("name"),
                    rs.getString("student_id"), rs.getString("email"), rs.getString("role")
                );
                mainFrame.loginAs(user);
            } else {
                messageLabel.setForeground(ThemeManager.DANGER);
                messageLabel.setText("Invalid credentials. Try again.");
            }
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }
}