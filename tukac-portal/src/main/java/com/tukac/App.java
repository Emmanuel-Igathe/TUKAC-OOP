package com.tukac;

import com.formdev.flatlaf.FlatLightLaf;
import com.tukac.db.Database;
import com.tukac.ui.panels.MainFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
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

        Database.initialize();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}