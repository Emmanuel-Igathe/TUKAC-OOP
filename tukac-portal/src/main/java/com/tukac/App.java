package com.tukac;

import com.formdev.flatlaf.FlatLightLaf;
import com.tukac.db.Database;
import com.tukac.ui.panels.LoginPanel;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Set modern look and feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);
        } catch (Exception e) {
            System.err.println("Failed to set look and feel");
        }

        // Initialize the database
        Database.initialize();

        // Run the GUI
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TUK Ability Club Portal");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);

            // Start with the login screen
            frame.add(new LoginPanel(frame));

            frame.setVisible(true);
        });
    }
}