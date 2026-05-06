package com.tukac.ui.panels;

import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel contentArea;
    private JPanel navBar;
    private JPanel navLinks;
    private JPanel authPanel;
    private User currentUser;
    private JButton activeNavBtn;

    public MainFrame() {
        setTitle("TUK Ability Club Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Nav bar
        navBar = new JPanel(new BorderLayout());
        navBar.setBackground(ThemeManager.BG_TOPBAR);
        navBar.setPreferredSize(new Dimension(0, 52));
        navBar.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));

        JLabel logo = new JLabel("TUK Ability Club");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(ThemeManager.TEXT_WHITE);
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { navigateTo("Home"); }
        });
        navBar.add(logo, BorderLayout.WEST);

        navLinks = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        navLinks.setOpaque(false);
        navBar.add(navLinks, BorderLayout.CENTER);

        authPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        authPanel.setOpaque(false);
        navBar.add(authPanel, BorderLayout.EAST);

        add(navBar, BorderLayout.NORTH);

        // Content
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ThemeManager.BG_MAIN);
        add(contentArea, BorderLayout.CENTER);

        buildGuestNav();
        navigateTo("Home");
    }

    // ═══════════════════════════════
    // NAVIGATION BUILDERS
    // ═══════════════════════════════
    private void buildGuestNav() {
        navLinks.removeAll();
        authPanel.removeAll();
        activeNavBtn = null;

        addNavBtn("Home");
        addNavBtn("Events");
        addNavBtn("Blog");
        addNavBtn("About Us");
        addNavBtn("Why Join Us");
        addNavBtn("Contact");

        JButton loginBtn = ThemeManager.createButton("Sign In", ThemeManager.PRIMARY);
        loginBtn.setPreferredSize(new Dimension(80, 32));
        loginBtn.addActionListener(e -> navigateTo("Login"));
        authPanel.add(loginBtn);

        JButton regBtn = ThemeManager.createButton("Register", ThemeManager.PURPLE);
        regBtn.setPreferredSize(new Dimension(80, 32));
        regBtn.addActionListener(e -> navigateTo("Register"));
        authPanel.add(regBtn);

        navLinks.revalidate();
        authPanel.revalidate();
    }

    private void buildMemberNav() {
        navLinks.removeAll();
        authPanel.removeAll();
        activeNavBtn = null;

        addNavBtn("Home");
        addNavBtn("Events");
        addNavBtn("My RSVPs");
        addNavBtn("Blog");
        addNavBtn("Finances");
        addNavBtn("Members");
        addNavBtn("About Us");
        addNavBtn("Contact");

        if (currentUser.isExecutive() || currentUser.isAdmin()) {
            JButton adminBtn = ThemeManager.createButton("Admin Panel", new Color(234, 179, 8));
            adminBtn.setPreferredSize(new Dimension(100, 32));
            adminBtn.addActionListener(e -> navigateTo("AdminPanel"));
            authPanel.add(adminBtn);
        }

        // User dropdown area
        JLabel dot = new JLabel("\u25CF ");
        dot.setForeground(ThemeManager.SUCCESS);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        authPanel.add(dot);

        JButton profileBtn = new JButton(currentUser.getName());
        profileBtn.setForeground(ThemeManager.TEXT_WHITE);
        profileBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        profileBtn.setBorderPainted(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setFocusPainted(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.addActionListener(e -> navigateTo("Profile"));
        authPanel.add(profileBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(new Color(252, 165, 165));
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        authPanel.add(logoutBtn);

        navLinks.revalidate();
        authPanel.revalidate();
    }

    private void addNavBtn(String name) {
        JButton btn = new JButton(name);
        btn.setForeground(new Color(209, 213, 219));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setForeground(ThemeManager.TEXT_WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeNavBtn) btn.setForeground(new Color(209, 213, 219));
            }
        });

        btn.addActionListener(e -> {
            if (activeNavBtn != null) {
                activeNavBtn.setForeground(new Color(209, 213, 219));
            }
            btn.setForeground(ThemeManager.TEXT_WHITE);
            activeNavBtn = btn;
            navigateTo(name);
        });

        navLinks.add(btn);
    }

    // ═══════════════════════════════
    // LOGIN / LOGOUT
    // ═══════════════════════════════
    public void loginAs(User user) {
        this.currentUser = user;
        buildMemberNav();
        navigateTo("Home");
    }

    public void logout() {
        this.currentUser = null;
        buildGuestNav();
        navigateTo("Home");
    }

    // ═══════════════════════════════
    // ROUTER
    // ═══════════════════════════════
    public void navigateTo(String page) {
        contentArea.removeAll();

        JPanel pageContent = switch (page) {
            case "Home" -> new SiteHomePage(this, currentUser);
            case "Events" -> wrapPage(new EventsPanel(currentUser));
            case "My RSVPs" -> wrapPage(currentUser != null ? new MyRsvpsPanel(currentUser) : new EventsPanel(null));
            case "Blog" -> wrapPage(currentUser != null ? new BlogPanel(currentUser) : new BlogPanel(null));
            case "Finances" -> wrapPage(new FinancesPanel(currentUser));
            case "Members" -> wrapPage(new MemberDirectoryPanel(currentUser));
            case "About Us" -> new SiteAboutPage();
            case "Why Join Us" -> new SiteWhyJoinPage();
            case "Contact" -> new SiteContactPage();
            case "Profile" -> wrapPage(currentUser != null ? new UserProfilePanel(currentUser) : new JPanel());
            case "Login" -> new SiteLoginPage(this);
            case "Register" -> new SiteRegisterPage(this);
            case "AdminPanel" -> {
                if (currentUser != null && (currentUser.isExecutive() || currentUser.isAdmin())) {
                    yield new DashboardPanel(this, currentUser);
                }
                yield new SiteHomePage(this, currentUser);
            }
            default -> new SiteHomePage(this, currentUser);
        };

        contentArea.add(pageContent, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel wrapPage(JPanel inner) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ThemeManager.BG_MAIN);

        JPanel padded = new JPanel(new BorderLayout());
        padded.setBackground(ThemeManager.BG_MAIN);
        padded.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        padded.add(inner, BorderLayout.CENTER);

        wrapper.add(padded, BorderLayout.CENTER);
        wrapper.add(createFooter(), BorderLayout.SOUTH);
        return wrapper;
    }

    public static JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(17, 24, 39));
        footer.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel f1 = new JLabel("TUK Ability Club Portal \u00A9 2026  |  Technical University of Kenya");
        f1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        f1.setForeground(new Color(156, 163, 175));
        f1.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(f1);
        footer.add(Box.createVerticalStrut(3));
        JLabel f2 = new JLabel("Built by The Three Muskateers — Margaret, Emmanuel, Terry");
        f2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        f2.setForeground(new Color(156, 163, 175));
        f2.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(f2);

        return footer;
    }
}