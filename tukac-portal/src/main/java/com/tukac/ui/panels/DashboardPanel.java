package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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

    public DashboardPanel(JFrame parentFrame, User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        // ===== TOP BAR =====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(ThemeManager.BG_TOPBAR);
        topBar.setPreferredSize(new Dimension(0, 55));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel appTitle = new JLabel("TUK Ability Club Portal");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(ThemeManager.TEXT_WHITE);
        topBar.add(appTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);
        JLabel userIcon = new JLabel("\u25CF ");
        userIcon.setForeground(ThemeManager.SUCCESS);
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userPanel.add(userIcon);
        JLabel userInfo = new JLabel(user.getName() + "  |  " + user.getRole().toUpperCase());
        userInfo.setForeground(ThemeManager.TEXT_WHITE);
        userInfo.setFont(ThemeManager.FONT_BODY);
        userPanel.add(userInfo);
        topBar.add(userPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ===== SIDEBAR =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        addSectionLabel(sidebar, "MAIN MENU");
        addMenuButton(sidebar, "\u2302  Home", () -> showHome());
        addMenuButton(sidebar, "\u2606  Events", () -> showPanel(new EventsPanel(currentUser)));
        addMenuButton(sidebar, "\u2665  My RSVPs", () -> showPanel(new MyRsvpsPanel(currentUser)));
        addMenuButton(sidebar, "\u0024  Finances", () -> showPanel(new FinancesPanel(currentUser)));
        addMenuButton(sidebar, "\u270E  Blog", () -> showPanel(new BlogPanel(currentUser)));
        addMenuButton(sidebar, "\u263A  Members", () -> showPanel(new MemberDirectoryPanel(currentUser)));
        addMenuButton(sidebar, "\u2139  About", () -> showPanel(new AboutPanel()));
        addMenuButton(sidebar, "\u003F  Help", () -> showPanel(new HelpPanel()));

        if (user.isExecutive()) {
            sidebar.add(Box.createVerticalStrut(15));
            addSectionLabel(sidebar, "MANAGEMENT");
            addMenuButton(sidebar, "\u2699  Manage Events", () -> showPanel(new ManageEventsPanel(currentUser)));
            addMenuButton(sidebar, "\u2699  Manage Blog", () -> showPanel(new ManageBlogPanel(currentUser)));
            addMenuButton(sidebar, "\u2699  Manage Finances", () -> showPanel(new ManageFinancesPanel(currentUser)));
        }

        if (user.isAdmin()) {
            sidebar.add(Box.createVerticalStrut(15));
            addSectionLabel(sidebar, "ADMIN");
            addMenuButton(sidebar, "\u26A0  Manage Users", () -> showPanel(new ManageUsersPanel(currentUser)));
        }

        sidebar.add(Box.createVerticalGlue());

        // Account section
        addSectionLabel(sidebar, "ACCOUNT");
        addMenuButton(sidebar, "\u263A  My Profile", () -> showPanel(new UserProfilePanel(currentUser)));

        sidebar.add(Box.createVerticalStrut(8));

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
        logoutBtn.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new LoginPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);

        // ===== CONTENT AREA =====
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ThemeManager.BG_MAIN);
        contentArea.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        add(contentArea, BorderLayout.CENTER);

        showHome();
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
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(ThemeManager.SIDEBAR_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton) btn.setBackground(ThemeManager.BG_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            if (activeButton != null) {
                activeButton.setBackground(ThemeManager.BG_SIDEBAR);
                activeButton.setForeground(new Color(209, 213, 219));
            }
            btn.setBackground(ThemeManager.SIDEBAR_ACTIVE);
            btn.setForeground(ThemeManager.TEXT_WHITE);
            activeButton = btn;
            action.run();
        });

        sidebar.add(btn);
    }

    private void showHome() {
        contentArea.removeAll();

        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(ThemeManager.BG_MAIN);

        JLabel welcome = new JLabel("Welcome back, " + currentUser.getName() + "!");
        welcome.setFont(ThemeManager.FONT_TITLE);
        welcome.setForeground(ThemeManager.TEXT_PRIMARY);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePanel.add(welcome);

        homePanel.add(Box.createVerticalStrut(5));

        JLabel role = new JLabel("Role: " + currentUser.getRole().toUpperCase() + "  \u2022  TUK Ability Club Dashboard");
        role.setFont(ThemeManager.FONT_BODY);
        role.setForeground(ThemeManager.TEXT_SECONDARY);
        role.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePanel.add(role);

        homePanel.add(Box.createVerticalStrut(30));

        // Live stats
        JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        cardsRow.setBackground(ThemeManager.BG_MAIN);
        cardsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        int eventCount = getCount("SELECT COUNT(*) FROM events");
        int blogCount = getCount("SELECT COUNT(*) FROM blog_posts");
        int memberCount = getCount("SELECT COUNT(*) FROM users WHERE is_approved = 1");
        double balance = getBalance();

        cardsRow.add(ThemeManager.createStatCard("Upcoming Events", String.valueOf(eventCount), ThemeManager.PRIMARY));
        cardsRow.add(ThemeManager.createStatCard("Blog Posts", String.valueOf(blogCount), ThemeManager.SUCCESS));
        cardsRow.add(ThemeManager.createStatCard("Club Balance", "KES " + String.format("%,.2f", balance), ThemeManager.PURPLE));

        homePanel.add(cardsRow);

        homePanel.add(Box.createVerticalStrut(20));

        // Second row
        JPanel cardsRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        cardsRow2.setBackground(ThemeManager.BG_MAIN);
        cardsRow2.setAlignmentX(Component.LEFT_ALIGNMENT);

        int myRsvps = getCount("SELECT COUNT(*) FROM event_registrations WHERE user_id = " + currentUser.getId());
        int pendingUsers = getCount("SELECT COUNT(*) FROM users WHERE is_approved = 0");

        cardsRow2.add(ThemeManager.createStatCard("Active Members", String.valueOf(memberCount), new Color(234, 179, 8)));
        cardsRow2.add(ThemeManager.createStatCard("My RSVPs", String.valueOf(myRsvps), new Color(6, 182, 212)));

        if (currentUser.isAdmin()) {
            cardsRow2.add(ThemeManager.createStatCard("Pending Approvals", String.valueOf(pendingUsers),
                pendingUsers > 0 ? ThemeManager.DANGER : ThemeManager.SUCCESS));
        }

        homePanel.add(cardsRow2);

        contentArea.add(homePanel, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private int getCount(String sql) {
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }

    private double getBalance() {
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT COALESCE(SUM(CASE WHEN type='income' THEN amount ELSE -amount END), 0) FROM transactions"
            );
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }

    private void showPanel(JPanel panel) {
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }
}