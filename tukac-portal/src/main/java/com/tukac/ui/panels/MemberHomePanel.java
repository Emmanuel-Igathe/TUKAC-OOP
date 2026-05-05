package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class MemberHomePanel extends JPanel {
    private User currentUser;
    private JFrame parentFrame;
    private JPanel contentArea;
    private JPanel navBar;
    private JButton activeNavBtn = null;

    public MemberHomePanel(JFrame parentFrame, User user) {
        this.currentUser = user;
        this.parentFrame = parentFrame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        // ===== TOP NAVIGATION =====
        navBar = new JPanel(new BorderLayout());
        navBar.setBackground(ThemeManager.BG_TOPBAR);
        navBar.setPreferredSize(new Dimension(0, 55));
        navBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel logo = new JLabel("TUK Ability Club");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(ThemeManager.TEXT_WHITE);
        logo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { showHomePage(); }
        });
        navBar.add(logo, BorderLayout.WEST);

        // Nav links
        JPanel navLinks = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        navLinks.setOpaque(false);

        addNavLink(navLinks, "Home", () -> showHomePage());
        addNavLink(navLinks, "Events", () -> showEventsPage());
        addNavLink(navLinks, "Blog", () -> showBlogPage());
        addNavLink(navLinks, "About Us", () -> showAboutPage());
        addNavLink(navLinks, "Why Join Us", () -> showWhyJoinPage());
        addNavLink(navLinks, "Contact", () -> showContactPage());

        navBar.add(navLinks, BorderLayout.CENTER);

        // User menu
        JPanel userMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        userMenu.setOpaque(false);

        JLabel userIcon = new JLabel("\u25CF");
        userIcon.setForeground(ThemeManager.SUCCESS);
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userMenu.add(userIcon);

        JLabel userName = new JLabel(user.getName());
        userName.setForeground(ThemeManager.TEXT_WHITE);
        userName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userMenu.add(userName);

        JButton profileBtn = new JButton("Profile");
        profileBtn.setForeground(new Color(147, 197, 253));
        profileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        profileBtn.setBorderPainted(false);
        profileBtn.setContentAreaFilled(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.addActionListener(e -> showProfilePage());
        userMenu.add(profileBtn);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(new Color(252, 165, 165));
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new WelcomeScreen(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        userMenu.add(logoutBtn);

        navBar.add(userMenu, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        // ===== CONTENT =====
        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(ThemeManager.BG_MAIN);
        add(contentArea, BorderLayout.CENTER);

        showHomePage();
    }

    private void addNavLink(JPanel panel, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setForeground(new Color(209, 213, 219));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(15, 14, 15, 14));

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
                activeNavBtn.setContentAreaFilled(false);
            }
            btn.setForeground(ThemeManager.TEXT_WHITE);
            activeNavBtn = btn;
            action.run();
        });

        panel.add(btn);
    }

    private void setContent(JPanel panel) {
        contentArea.removeAll();
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        contentArea.add(scroll, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ═══════════════════════════════════════
    // HOME PAGE
    // ═══════════════════════════════════════
    private void showHomePage() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);

        // Hero
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(new Color(30, 58, 138));
        hero.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel hi = new JLabel("Welcome back, " + currentUser.getName() + "!");
        hi.setFont(new Font("Segoe UI", Font.BOLD, 26));
        hi.setForeground(ThemeManager.TEXT_WHITE);
        hi.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.add(hi);
        hero.add(Box.createVerticalStrut(8));

        JLabel sub = new JLabel("You are a valued member of the TUK Ability Club. Explore events, read stories, and stay connected.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(new Color(147, 197, 253));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.add(sub);

        page.add(hero);

        // Stats
        page.add(Box.createVerticalStrut(25));
        JPanel statsWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        statsWrap.setBackground(ThemeManager.BG_MAIN);
        statsWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        int myRsvps = getCount("SELECT COUNT(*) FROM event_registrations WHERE user_id = " + currentUser.getId());
        int eventCount = getCount("SELECT COUNT(*) FROM events");
        int blogCount = getCount("SELECT COUNT(*) FROM blog_posts");
        int memberCount = getCount("SELECT COUNT(*) FROM users WHERE is_approved = 1");

        statsWrap.add(ThemeManager.createStatCard("My RSVPs", String.valueOf(myRsvps), new Color(6, 182, 212)));
        statsWrap.add(ThemeManager.createStatCard("Upcoming Events", String.valueOf(eventCount), ThemeManager.PRIMARY));
        statsWrap.add(ThemeManager.createStatCard("Blog Posts", String.valueOf(blogCount), ThemeManager.SUCCESS));
        statsWrap.add(ThemeManager.createStatCard("Club Members", String.valueOf(memberCount), ThemeManager.PURPLE));

        page.add(statsWrap);

        // Upcoming events
        page.add(Box.createVerticalStrut(30));
        page.add(createSectionHeader("Upcoming Events", "Browse and RSVP to club events"));

        JPanel eventsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        eventsRow.setBackground(ThemeManager.BG_MAIN);
        eventsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        eventsRow.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM events ORDER BY event_date ASC LIMIT 3");
            int c = 0;
            while (rs.next()) {
                c++;
                JPanel card = ThemeManager.createCard();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                JLabel t = new JLabel(rs.getString("title"));
                t.setFont(new Font("Segoe UI", Font.BOLD, 14));
                t.setForeground(ThemeManager.TEXT_PRIMARY);
                t.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(t);
                card.add(Box.createVerticalStrut(6));
                card.add(makeDetail("Date: " + rs.getString("event_date")));
                card.add(makeDetail("Time: " + (rs.getString("event_time") != null ? rs.getString("event_time") : "TBD")));
                card.add(makeDetail("Location: " + (rs.getString("location") != null ? rs.getString("location") : "TBD")));
                card.add(Box.createVerticalStrut(8));
                JButton rsvpBtn = ThemeManager.createButton("RSVP", ThemeManager.PRIMARY);
                rsvpBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                int eventId = rs.getInt("id");
                rsvpBtn.addActionListener(e -> {
                    try {
                        PreparedStatement ps = Database.getConnection().prepareStatement("INSERT OR IGNORE INTO event_registrations (user_id, event_id) VALUES (?, ?)");
                        ps.setInt(1, currentUser.getId());
                        ps.setInt(2, eventId);
                        int rows = ps.executeUpdate();
                        JOptionPane.showMessageDialog(this, rows > 0 ? "RSVP successful!" : "Already RSVP'd!");
                    } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
                });
                card.add(rsvpBtn);
                eventsRow.add(card);
            }
            if (c == 0) { eventsRow.add(new JLabel("No upcoming events.")); }
            while (c < 3) { eventsRow.add(new JPanel() {{ setBackground(ThemeManager.BG_MAIN); }}); c++; }
        } catch (SQLException e) { System.err.println(e.getMessage()); }

        page.add(eventsRow);

        // Recent blog posts
        page.add(Box.createVerticalStrut(30));
        page.add(createSectionHeader("Latest Blog Posts", "Stories, announcements, and updates from the club"));

        JPanel blogRow = new JPanel(new GridLayout(1, 3, 15, 0));
        blogRow.setBackground(ThemeManager.BG_MAIN);
        blogRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        blogRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        blogRow.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT bp.*, u.name as author_name FROM blog_posts bp LEFT JOIN users u ON bp.author_id = u.id ORDER BY bp.published_at DESC LIMIT 3");
            int c = 0;
            while (rs.next()) {
                c++;
                JPanel card = ThemeManager.createCard();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                String cat = rs.getString("category");
                if (cat == null) cat = "General";
                JLabel badge = new JLabel(" " + cat.toUpperCase() + " ");
                badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
                badge.setForeground(ThemeManager.TEXT_WHITE);
                badge.setOpaque(true);
                badge.setBackground(ThemeManager.PRIMARY);
                badge.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(badge);
                card.add(Box.createVerticalStrut(6));
                JLabel tt = new JLabel(rs.getString("title"));
                tt.setFont(new Font("Segoe UI", Font.BOLD, 13));
                tt.setForeground(ThemeManager.TEXT_PRIMARY);
                tt.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(tt);
                card.add(Box.createVerticalStrut(4));
                String body = rs.getString("body");
                String preview = body.length() > 70 ? body.substring(0, 70) + "..." : body;
                card.add(makeDetail(preview));
                card.add(Box.createVerticalStrut(4));
                String auth = rs.getString("author_name");
                card.add(makeDetail("By " + (auth != null ? auth : "Unknown")));
                blogRow.add(card);
            }
            if (c == 0) { blogRow.add(new JLabel("No blog posts yet.")); }
            while (c < 3) { blogRow.add(new JPanel() {{ setBackground(ThemeManager.BG_MAIN); }}); c++; }
        } catch (SQLException e) { System.err.println(e.getMessage()); }

        page.add(blogRow);
        page.add(Box.createVerticalStrut(30));
        page.add(createFooter());

        setContent(page);
    }

    // ═══════════════════════════════════════
    // EVENTS PAGE
    // ═══════════════════════════════════════
    private void showEventsPage() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ThemeManager.BG_MAIN);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(new EventsPanel(currentUser), BorderLayout.CENTER);
        setContent(wrapper);
    }

    // ═══════════════════════════════════════
    // BLOG PAGE
    // ═══════════════════════════════════════
    private void showBlogPage() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ThemeManager.BG_MAIN);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(new BlogPanel(currentUser), BorderLayout.CENTER);
        setContent(wrapper);
    }

    // ═══════════════════════════════════════
    // ABOUT US PAGE
    // ═══════════════════════════════════════
    private void showAboutPage() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JPanel headerBanner = new JPanel();
        headerBanner.setLayout(new BoxLayout(headerBanner, BoxLayout.Y_AXIS));
        headerBanner.setBackground(new Color(30, 58, 138));
        headerBanner.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        headerBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel h1 = new JLabel("About TUK Ability Club");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        h1.setForeground(ThemeManager.TEXT_WHITE);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.add(h1);
        headerBanner.add(Box.createVerticalStrut(8));
        JLabel h2 = new JLabel("Empowering students with disabilities since 2024");
        h2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        h2.setForeground(new Color(147, 197, 253));
        h2.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.add(h2);

        page.add(headerBanner);
        page.add(Box.createVerticalStrut(20));

        page.add(createInfoSection("Our Mission",
            "To empower students with disabilities at the Technical University of Kenya through advocacy, support, community building, and inclusive opportunities that enable every student to achieve their full potential.",
            ThemeManager.PRIMARY));
        page.add(Box.createVerticalStrut(15));

        page.add(createInfoSection("Our Vision",
            "An inclusive university environment where every student thrives regardless of ability — where barriers are removed, voices are heard, and diversity is celebrated as strength.",
            ThemeManager.PURPLE));
        page.add(Box.createVerticalStrut(15));

        page.add(createInfoSection("Our History",
            "Founded in 2024 by a group of passionate students at the Technical University of Kenya, the Ability Club started as a small support group. Today, we have grown into a vibrant community that advocates for disability rights, organizes inclusive events, and provides academic and social support to all members.",
            ThemeManager.SUCCESS));
        page.add(Box.createVerticalStrut(15));

        // Leadership
        page.add(createSectionTitle("Leadership Team"));
        page.add(Box.createVerticalStrut(10));

        JPanel leaders = new JPanel(new GridLayout(1, 3, 15, 0));
        leaders.setBackground(ThemeManager.BG_MAIN);
        leaders.setAlignmentX(Component.LEFT_ALIGNMENT);
        leaders.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        leaders.add(createLeaderCard("Chairperson", "Margaret Wambui Nduta", ThemeManager.PRIMARY));
        leaders.add(createLeaderCard("Vice Chairperson", "Mararo Emmanuel Igathe", ThemeManager.PURPLE));
        leaders.add(createLeaderCard("Secretary", "Terry Mwikali", ThemeManager.SUCCESS));

        page.add(leaders);
        page.add(Box.createVerticalStrut(30));
        page.add(createFooter());

        setContent(page);
    }

    // ═══════════════════════════════════════
    // WHY JOIN US PAGE
    // ═══════════════════════════════════════
    private void showWhyJoinPage() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel headerBanner = new JPanel();
        headerBanner.setLayout(new BoxLayout(headerBanner, BoxLayout.Y_AXIS));
        headerBanner.setBackground(new Color(88, 28, 135));
        headerBanner.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        headerBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel h1 = new JLabel("Why Join TUK Ability Club?");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        h1.setForeground(ThemeManager.TEXT_WHITE);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.add(h1);
        headerBanner.add(Box.createVerticalStrut(8));
        JLabel h2 = new JLabel("Discover the benefits of being part of our community");
        h2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        h2.setForeground(new Color(216, 180, 254));
        h2.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.add(h2);
        page.add(headerBanner);
        page.add(Box.createVerticalStrut(20));

        String[][] benefits = {
            {"Community & Belonging", "Join a supportive family of students who understand your journey. Build lifelong friendships and connections in a safe, welcoming space.", "\u2764"},
            {"Advocacy & Representation", "Your voice matters. We advocate for accessible facilities, inclusive policies, and equal opportunities at TUK and beyond.", "\u2694"},
            {"Skills Development", "Attend workshops on leadership, technology, communication, and career readiness. Build skills that set you apart.", "\u2605"},
            {"Events & Social Life", "From awareness weeks to sports days, talent shows to study groups — there's always something happening. Never miss out.", "\u266B"},
            {"Academic Support", "Get peer tutoring, study groups, and academic mentorship from fellow members and university staff.", "\u270E"},
            {"Networking", "Connect with alumni, professionals, and partner organizations. Open doors for internships, mentorship, and career opportunities.", "\u263A"},
        };

        JPanel grid = new JPanel(new GridLayout(3, 2, 15, 15));
        grid.setBackground(ThemeManager.BG_MAIN);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));

        Color[] colors = {ThemeManager.PRIMARY, ThemeManager.PURPLE, ThemeManager.SUCCESS, new Color(234, 179, 8), new Color(6, 182, 212), ThemeManager.DANGER};

        for (int i = 0; i < benefits.length; i++) {
            JPanel card = ThemeManager.createCard();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, colors[i]),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)
                )
            ));

            JLabel icon = new JLabel(benefits[i][2]);
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            icon.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(icon);
            card.add(Box.createVerticalStrut(8));

            JLabel title = new JLabel(benefits[i][0]);
            title.setFont(new Font("Segoe UI", Font.BOLD, 15));
            title.setForeground(ThemeManager.TEXT_PRIMARY);
            title.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(title);
            card.add(Box.createVerticalStrut(6));

            JTextArea desc = new JTextArea(benefits[i][1]);
            desc.setFont(ThemeManager.FONT_BODY);
            desc.setForeground(ThemeManager.TEXT_SECONDARY);
            desc.setLineWrap(true);
            desc.setWrapStyleWord(true);
            desc.setEditable(false);
            desc.setBackground(ThemeManager.BG_CARD);
            desc.setBorder(null);
            desc.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(desc);

            grid.add(card);
        }

        page.add(grid);
        page.add(Box.createVerticalStrut(30));
        page.add(createFooter());

        setContent(page);
    }

    // ═══════════════════════════════════════
    // CONTACT PAGE
    // ═══════════════════════════════════════
    private void showContactPage() {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel headerBanner = new JPanel();
        headerBanner.setLayout(new BoxLayout(headerBanner, BoxLayout.Y_AXIS));
        headerBanner.setBackground(new Color(22, 101, 52));
        headerBanner.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        headerBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel h1 = new JLabel("Contact Us");
        h1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        h1.setForeground(ThemeManager.TEXT_WHITE);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.add(h1);
        headerBanner.add(Box.createVerticalStrut(8));
        JLabel h2 = new JLabel("We'd love to hear from you. Reach out anytime!");
        h2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        h2.setForeground(new Color(187, 247, 208));
        h2.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerBanner.add(h2);
        page.add(headerBanner);
        page.add(Box.createVerticalStrut(25));

        // Contact cards
        JPanel contactGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        contactGrid.setBackground(ThemeManager.BG_MAIN);
        contactGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        contactGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        contactGrid.add(createContactCard("Email", "abilityclub@tuk.ac.ke", "Send us an email anytime", ThemeManager.PRIMARY));
        contactGrid.add(createContactCard("Phone", "+254 700 000 000", "Call us during office hours", ThemeManager.SUCCESS));
        contactGrid.add(createContactCard("Office", "Student Center, Room 105", "Visit us on campus", ThemeManager.PURPLE));
        contactGrid.add(createContactCard("Meeting Times", "Every Wednesday 2-4 PM", "Join our weekly meetings", new Color(234, 179, 8)));

        page.add(contactGrid);
        page.add(Box.createVerticalStrut(25));

        // Social / additional info
        page.add(createInfoSection("Find Us on Campus",
            "Our office is located on the ground floor of the Student Center, Room 105. The office is fully accessible with ramp access and wide doorways. We are open Monday to Friday, 9:00 AM to 5:00 PM. Drop by anytime — no appointment needed!\n\nFor urgent matters outside office hours, please email us at abilityclub@tuk.ac.ke and we will respond within 24 hours.",
            ThemeManager.SUCCESS));

        page.add(Box.createVerticalStrut(30));
        page.add(createFooter());

        setContent(page);
    }

    // ═══════════════════════════════════════
    // PROFILE PAGE
    // ═══════════════════════════════════════
    private void showProfilePage() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ThemeManager.BG_MAIN);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapper.add(new UserProfilePanel(currentUser), BorderLayout.CENTER);
        setContent(wrapper);
    }

    // ═══════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════
    private JLabel makeDetail(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_SMALL);
        l.setForeground(ThemeManager.TEXT_SECONDARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel createSectionHeader(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t = new JLabel(title);
        t.setFont(ThemeManager.FONT_HEADING);
        t.setForeground(ThemeManager.TEXT_PRIMARY);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(t);

        if (subtitle != null) {
            panel.add(Box.createVerticalStrut(4));
            JLabel s = new JLabel(subtitle);
            s.setFont(ThemeManager.FONT_BODY);
            s.setForeground(ThemeManager.TEXT_SECONDARY);
            s.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(s);
        }
        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_HEADING);
        l.setForeground(ThemeManager.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel createInfoSection(String title, String body, Color accent) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)
            )
        ));

        JLabel t = new JLabel(title);
        t.setFont(ThemeManager.FONT_SUBHEADING);
        t.setForeground(ThemeManager.TEXT_PRIMARY);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(10));

        JTextArea b = new JTextArea(body);
        b.setFont(ThemeManager.FONT_BODY);
        b.setForeground(ThemeManager.TEXT_SECONDARY);
        b.setLineWrap(true);
        b.setWrapStyleWord(true);
        b.setEditable(false);
        b.setBackground(ThemeManager.BG_CARD);
        b.setBorder(null);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(b);

        return card;
    }

    private JPanel createLeaderCard(String role, String name, Color color) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        String initials = "";
        for (String p : name.split(" ")) if (!p.isEmpty()) initials += p.charAt(0);
        if (initials.length() > 2) initials = initials.substring(0, 2);

        JLabel avatar = new JLabel(initials, SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(44, 44));
        avatar.setMaximumSize(new Dimension(44, 44));
        avatar.setOpaque(true);
        avatar.setBackground(color);
        avatar.setForeground(ThemeManager.TEXT_WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(avatar);
        card.add(Box.createVerticalStrut(8));

        JLabel r = new JLabel(role);
        r.setFont(ThemeManager.FONT_SMALL);
        r.setForeground(ThemeManager.TEXT_SECONDARY);
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(r);

        JLabel n = new JLabel(name);
        n.setFont(new Font("Segoe UI", Font.BOLD, 14));
        n.setForeground(color);
        n.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(n);

        return card;
    }

    private JPanel createContactCard(String title, String value, String desc, Color accent) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, accent),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
            )
        ));

        JLabel t = new JLabel(title);
        t.setFont(ThemeManager.FONT_SMALL);
        t.setForeground(ThemeManager.TEXT_SECONDARY);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(t);
        card.add(Box.createVerticalStrut(6));

        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 15));
        v.setForeground(ThemeManager.TEXT_PRIMARY);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(v);
        card.add(Box.createVerticalStrut(4));

        JLabel d = new JLabel(desc);
        d.setFont(ThemeManager.FONT_SMALL);
        d.setForeground(ThemeManager.TEXT_LIGHT);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(d);

        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(17, 24, 39));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel f1 = new JLabel("TUK Ability Club Portal \u00A9 2026  |  Technical University of Kenya");
        f1.setFont(ThemeManager.FONT_SMALL);
        f1.setForeground(ThemeManager.TEXT_LIGHT);
        f1.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(f1);
        footer.add(Box.createVerticalStrut(4));
        JLabel f2 = new JLabel("Built by The Three Muskateers");
        f2.setFont(ThemeManager.FONT_SMALL);
        f2.setForeground(ThemeManager.TEXT_LIGHT);
        f2.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(f2);

        return footer;
    }

    private int getCount(String sql) {
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }
}