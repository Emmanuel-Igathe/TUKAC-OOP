package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

public class SiteHomePage extends JPanel {
    private User user;
    private MainFrame mainFrame;

    public SiteHomePage(MainFrame mainFrame, User user) {
        this.user = user;
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);

        // Hero
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(new Color(30, 58, 138));
        hero.setBorder(BorderFactory.createEmptyBorder(45, 50, 45, 50));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        String greeting = user != null ? "Welcome back, " + user.getName() + "!" : "Welcome to TUK Ability Club";
        JLabel h1 = new JLabel(greeting);
        h1.setFont(new Font("Segoe UI", Font.BOLD, 28));
        h1.setForeground(ThemeManager.TEXT_WHITE);
        h1.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.add(h1);
        hero.add(Box.createVerticalStrut(10));

        JLabel h2 = new JLabel("Empowering students with disabilities at the Technical University of Kenya");
        h2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        h2.setForeground(new Color(147, 197, 253));
        h2.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.add(h2);

        if (user == null) {
            hero.add(Box.createVerticalStrut(18));
            JPanel heroBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            heroBtns.setOpaque(false);
            heroBtns.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton joinBtn = ThemeManager.createButton("  Join the Club  ", ThemeManager.SUCCESS);
            joinBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            joinBtn.addActionListener(e -> mainFrame.navigateTo("Register"));
            heroBtns.add(joinBtn);

            JButton learnBtn = ThemeManager.createButton("  Learn More  ", new Color(255, 255, 255, 40));
            learnBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            learnBtn.addActionListener(e -> mainFrame.navigateTo("About Us"));
            heroBtns.add(learnBtn);

            hero.add(heroBtns);
        }

        page.add(hero);

        // Stats
        page.add(Box.createVerticalStrut(25));
        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setBackground(ThemeManager.BG_MAIN);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        int members = getCount("SELECT COUNT(*) FROM users WHERE is_approved = 1");
        int events = getCount("SELECT COUNT(*) FROM events");
        int blogs = getCount("SELECT COUNT(*) FROM blog_posts");

        statsRow.add(ThemeManager.createStatCard("Active Members", String.valueOf(members), ThemeManager.PRIMARY));
        statsRow.add(ThemeManager.createStatCard("Events Hosted", String.valueOf(events), ThemeManager.SUCCESS));
        statsRow.add(ThemeManager.createStatCard("Blog Posts", String.valueOf(blogs), ThemeManager.PURPLE));

        if (user != null) {
            int myRsvps = getCount("SELECT COUNT(*) FROM event_registrations WHERE user_id = " + user.getId());
            statsRow.add(ThemeManager.createStatCard("My RSVPs", String.valueOf(myRsvps), new Color(6, 182, 212)));
        } else {
            statsRow.add(ThemeManager.createStatCard("Founded", "2024", new Color(234, 179, 8)));
        }
        page.add(statsRow);

        // Events section
        page.add(Box.createVerticalStrut(30));
        page.add(sectionHeader("Upcoming Events", "Browse and RSVP to club events"));
        page.add(Box.createVerticalStrut(10));

        JPanel eventsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        eventsRow.setBackground(ThemeManager.BG_MAIN);
        eventsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        eventsRow.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM events ORDER BY event_date ASC LIMIT 3");
            int c = 0;
            while (rs.next()) {
                c++;
                JPanel card = ThemeManager.createCard();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                addLabel(card, rs.getString("title"), new Font("Segoe UI", Font.BOLD, 14), ThemeManager.TEXT_PRIMARY);
                card.add(Box.createVerticalStrut(6));
                addLabel(card, "Date: " + rs.getString("event_date"), ThemeManager.FONT_SMALL, ThemeManager.TEXT_SECONDARY);
                addLabel(card, "Time: " + nvl(rs.getString("event_time")), ThemeManager.FONT_SMALL, ThemeManager.TEXT_SECONDARY);
                addLabel(card, "Location: " + nvl(rs.getString("location")), ThemeManager.FONT_SMALL, ThemeManager.TEXT_SECONDARY);

                if (user != null) {
                    card.add(Box.createVerticalStrut(8));
                    int eid = rs.getInt("id");
                    JButton rsvp = ThemeManager.createButton("RSVP", ThemeManager.PRIMARY);
                    rsvp.setAlignmentX(Component.LEFT_ALIGNMENT);
                    rsvp.addActionListener(e -> {
                        try {
                            PreparedStatement ps = Database.getConnection().prepareStatement("INSERT OR IGNORE INTO event_registrations (user_id, event_id) VALUES (?,?)");
                            ps.setInt(1, user.getId()); ps.setInt(2, eid); int r = ps.executeUpdate();
                            JOptionPane.showMessageDialog(this, r > 0 ? "RSVP successful!" : "Already RSVP'd!");
                        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
                    });
                    card.add(rsvp);
                }
                eventsRow.add(card);
            }
            if (c == 0) eventsRow.add(emptyLabel("No upcoming events yet."));
            while (c < 3) { eventsRow.add(filler()); c++; }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        page.add(eventsRow);

        // Blog section
        page.add(Box.createVerticalStrut(30));
        page.add(sectionHeader("Latest Blog Posts", "Stories, announcements, and updates"));
        page.add(Box.createVerticalStrut(10));

        JPanel blogRow = new JPanel(new GridLayout(1, 3, 15, 0));
        blogRow.setBackground(ThemeManager.BG_MAIN);
        blogRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        blogRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        blogRow.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT bp.*, u.name as author FROM blog_posts bp LEFT JOIN users u ON bp.author_id = u.id ORDER BY bp.published_at DESC LIMIT 3");
            int c = 0;
            while (rs.next()) {
                c++;
                JPanel card = ThemeManager.createCard();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                String cat = nvl(rs.getString("category"));
                JLabel badge = new JLabel(" " + cat.toUpperCase() + " ");
                badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
                badge.setForeground(ThemeManager.TEXT_WHITE);
                badge.setOpaque(true);
                badge.setBackground(ThemeManager.PRIMARY);
                badge.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.add(badge);
                card.add(Box.createVerticalStrut(6));
                addLabel(card, rs.getString("title"), new Font("Segoe UI", Font.BOLD, 13), ThemeManager.TEXT_PRIMARY);
                card.add(Box.createVerticalStrut(4));
                String body = rs.getString("body");
                addLabel(card, body.length() > 80 ? body.substring(0, 80) + "..." : body, ThemeManager.FONT_SMALL, ThemeManager.TEXT_SECONDARY);
                card.add(Box.createVerticalStrut(4));
                addLabel(card, "By " + nvl(rs.getString("author")), ThemeManager.FONT_SMALL, ThemeManager.TEXT_LIGHT);
                blogRow.add(card);
            }
            if (c == 0) blogRow.add(emptyLabel("No blog posts yet."));
            while (c < 3) { blogRow.add(filler()); c++; }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        page.add(blogRow);

        page.add(Box.createVerticalStrut(30));
        page.add(MainFrame.createFooter());

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void addLabel(JPanel p, String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
    }

    private String nvl(String s) { return s != null ? s : "TBD"; }
    private JLabel emptyLabel(String t) { JLabel l = new JLabel(t); l.setFont(ThemeManager.FONT_BODY); l.setForeground(ThemeManager.TEXT_SECONDARY); return l; }
    private JPanel filler() { JPanel p = new JPanel(); p.setBackground(ThemeManager.BG_MAIN); return p; }
    private JPanel sectionHeader(String title, String sub) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBackground(ThemeManager.BG_MAIN);
        p.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30)); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel t = new JLabel(title); t.setFont(ThemeManager.FONT_HEADING); t.setForeground(ThemeManager.TEXT_PRIMARY); t.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(t);
        if (sub != null) { p.add(Box.createVerticalStrut(4)); JLabel s = new JLabel(sub); s.setFont(ThemeManager.FONT_BODY); s.setForeground(ThemeManager.TEXT_SECONDARY); s.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(s); }
        return p;
    }
    private int getCount(String sql) { try { ResultSet rs = Database.getConnection().createStatement().executeQuery(sql); if (rs.next()) return rs.getInt(1); } catch (SQLException e) {} return 0; }
}