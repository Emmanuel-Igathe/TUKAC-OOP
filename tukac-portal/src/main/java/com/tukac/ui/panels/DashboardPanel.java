package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class DashboardPanel extends JPanel {
    private JPanel contentArea;
    private User currentUser;
    private JButton activeButton = null;
    private MainFrame mainFrame;

    public DashboardPanel(MainFrame mainFrame, User user) {
        this.currentUser = user;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Back to website button
        JButton backBtn = new JButton("\u2190  Back to Website");
        backBtn.setMaximumSize(new Dimension(200, 38));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.setBackground(ThemeManager.PRIMARY);
        backBtn.setForeground(ThemeManager.TEXT_WHITE);
        backBtn.setFont(ThemeManager.FONT_BUTTON);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backBtn.addActionListener(e -> mainFrame.navigateTo("Home"));
        sidebar.add(backBtn);
        sidebar.add(Box.createVerticalStrut(15));

        addSectionLabel(sidebar, "MANAGEMENT");
        addMenuButton(sidebar, "\u2699  Manage Events", () -> showPanel(new ManageEventsPanel(currentUser)));
        addMenuButton(sidebar, "\u2699  Manage Blog", () -> showPanel(new ManageBlogPanel(currentUser)));
        addMenuButton(sidebar, "\u2699  Manage Finances", () -> showPanel(new ManageFinancesPanel(currentUser)));

        if (user.isAdmin()) {
            sidebar.add(Box.createVerticalStrut(15));
            addSectionLabel(sidebar, "ADMIN");
            int pending = getCount("SELECT COUNT(*) FROM users WHERE is_approved = 0");
            String label = pending > 0 ? "\u26A0  Manage Users (" + pending + ")" : "\u26A0  Manage Users";
            addMenuButton(sidebar, label, () -> showPanel(new ManageUsersPanel(currentUser)));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = new JButton("\u2190  Logout");
        logoutBtn.setMaximumSize(new Dimension(200, 40));
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setBackground(ThemeManager.DANGER);
        logoutBtn.setForeground(ThemeManager.TEXT_WHITE);
        logoutBtn.setFont(ThemeManager.FONT_BUTTON);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutBtn.addActionListener(e -> mainFrame.logout());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // ===== CONTENT AREA =====
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ThemeManager.BG_MAIN);
        contentArea.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        add(contentArea, BorderLayout.CENTER);

        // Show manage events by default
        showPanel(new ManageEventsPanel(currentUser));
    }

    private void addSectionLabel(JPanel sidebar, String text) {
        JLabel label = new JLabel("  " + text);
        label.setFont(ThemeManager.FONT_SIDEBAR_LABEL);
        label.setForeground(ThemeManager.TEXT_LIGHT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));
        sidebar.add(label);
    }

    private void addMenuButton(JPanel sidebar, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(ThemeManager.BG_SIDEBAR);
        btn.setForeground(new Color(209, 213, 219));
        btn.setFont(ThemeManager.FONT_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { if (btn != activeButton) btn.setBackground(ThemeManager.SIDEBAR_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e) { if (btn != activeButton) btn.setBackground(ThemeManager.BG_SIDEBAR); }
        });

        btn.addActionListener(e -> {
            if (activeButton != null) { activeButton.setBackground(ThemeManager.BG_SIDEBAR); activeButton.setForeground(new Color(209, 213, 219)); }
            btn.setBackground(ThemeManager.SIDEBAR_ACTIVE); btn.setForeground(ThemeManager.TEXT_WHITE); activeButton = btn;
            action.run();
        });
        sidebar.add(btn);
    }

    private void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private int getCount(String sql) {
        try { ResultSet rs = Database.getConnection().createStatement().executeQuery(sql); if (rs.next()) return rs.getInt(1); } catch (SQLException e) {} return 0;
    }
}