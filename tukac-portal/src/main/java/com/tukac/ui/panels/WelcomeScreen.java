package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.tukac.db.Database;
import com.tukac.ui.ThemeManager;

public class WelcomeScreen extends JPanel {

    public WelcomeScreen(JFrame parentFrame) {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        // ===== TOP NAV BAR =====
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(ThemeManager.BG_TOPBAR);
        navBar.setPreferredSize(new Dimension(0, 55));
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel appTitle = new JLabel("TUK Ability Club Portal");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        appTitle.setForeground(ThemeManager.TEXT_WHITE);
        navBar.add(appTitle, BorderLayout.WEST);

        JPanel authButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        authButtons.setOpaque(false);

        JButton loginBtn = ThemeManager.createButton("  Sign In  ", ThemeManager.PRIMARY);
        loginBtn.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new LoginPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        authButtons.add(loginBtn);

        JButton registerBtn = ThemeManager.createButton("  Register  ", ThemeManager.PURPLE);
        registerBtn.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new RegisterPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        authButtons.add(registerBtn);

        navBar.add(authButtons, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        // ===== SCROLLABLE CONTENT =====
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ThemeManager.BG_MAIN);
        content.setBorder(BorderFactory.createEmptyBorder(0, 30, 30, 30));

        // ===== HERO SECTION =====
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(ThemeManager.BG_TOPBAR);
        hero.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel heroTitle = new JLabel("Welcome to TUK Ability Club");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heroTitle.setForeground(ThemeManager.TEXT_WHITE);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.add(heroTitle);

        hero.add(Box.createVerticalStrut(10));

        JLabel heroSub = new JLabel("Empowering students with disabilities at the Technical University of Kenya");
        heroSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        heroSub.setForeground(new Color(147, 197, 253));
        heroSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.add(heroSub);

        hero.add(Box.createVerticalStrut(8));

        JLabel heroSub2 = new JLabel("Through advocacy, support, community building, and inclusive opportunities");
        heroSub2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        heroSub2.setForeground(new Color(147, 197, 253));
        heroSub2.setAlignmentX(Component.CENTER_ALIGNMENT);
        hero.add(heroSub2);

        hero.add(Box.createVerticalStrut(20));

        JButton joinBtn = ThemeManager.createButton("    Join the Club    ", ThemeManager.SUCCESS);
        joinBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        joinBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        joinBtn.addActionListener(e -> {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(new RegisterPanel(parentFrame));
            parentFrame.revalidate();
            parentFrame.repaint();
        });
        hero.add(joinBtn);

        content.add(Box.createVerticalStrut(20));
        content.add(hero);

        // ===== STATS ROW =====
        content.add(Box.createVerticalStrut(25));

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 15, 0));
        statsRow.setBackground(ThemeManager.BG_MAIN);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        int memberCount = getCount("SELECT COUNT(*) FROM users WHERE is_approved = 1");
        int eventCount = getCount("SELECT COUNT(*) FROM events");
        int blogCount = getCount("SELECT COUNT(*) FROM blog_posts");

        statsRow.add(ThemeManager.createStatCard("Active Members", String.valueOf(memberCount), ThemeManager.PRIMARY));
        statsRow.add(ThemeManager.createStatCard("Events Hosted", String.valueOf(eventCount), ThemeManager.SUCCESS));
        statsRow.add(ThemeManager.createStatCard("Blog Posts", String.valueOf(blogCount), ThemeManager.PURPLE));
        statsRow.add(ThemeManager.createStatCard("Founded", "2024", new Color(234, 179, 8)));

        content.add(statsRow);

        // ===== UPCOMING EVENTS =====
        content.add(Box.createVerticalStrut(30));

        JLabel eventsTitle = new JLabel("Upcoming Events");
        eventsTitle.setFont(ThemeManager.FONT_HEADING);
        eventsTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        eventsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(eventsTitle);

        content.add(Box.createVerticalStrut(12));

        JPanel eventsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        eventsRow.setBackground(ThemeManager.BG_MAIN);
        eventsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM events ORDER BY event_date ASC LIMIT 3");
            int count = 0;
            while (rs.next()) {
                count++;
                eventsRow.add(createEventCard(
                    rs.getString("title"),
                    rs.getString("event_date"),
                    rs.getString("event_time"),
                    rs.getString("location")
                ));
            }
            if (count == 0) {
                JLabel noEvents = new JLabel("No upcoming events yet. Check back soon!");
                noEvents.setFont(ThemeManager.FONT_BODY);
                noEvents.setForeground(ThemeManager.TEXT_SECONDARY);
                noEvents.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(noEvents);
            } else {
                content.add(eventsRow);
            }
        } catch (SQLException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }

        // ===== RECENT BLOG POSTS =====
        content.add(Box.createVerticalStrut(30));

        JLabel blogTitle = new JLabel("Recent Blog Posts");
        blogTitle.setFont(ThemeManager.FONT_HEADING);
        blogTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        blogTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(blogTitle);

        content.add(Box.createVerticalStrut(12));

        JPanel blogRow = new JPanel(new GridLayout(1, 3, 15, 0));
        blogRow.setBackground(ThemeManager.BG_MAIN);
        blogRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        blogRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("""
                SELECT bp.*, u.name as author_name
                FROM blog_posts bp
                LEFT JOIN users u ON bp.author_id = u.id
                ORDER BY bp.published_at DESC LIMIT 3
            """);
            int count = 0;
            while (rs.next()) {
                count++;
                String author = rs.getString("author_name");
                if (author == null) author = "Unknown";
                String body = rs.getString("body");
                String preview = body.length() > 80 ? body.substring(0, 80) + "..." : body;
                blogRow.add(createBlogCard(
                    rs.getString("title"),
                    rs.getString("category"),
                    author,
                    preview
                ));
            }
            if (count == 0) {
                JLabel noPosts = new JLabel("No blog posts yet. Check back soon!");
                noPosts.setFont(ThemeManager.FONT_BODY);
                noPosts.setForeground(ThemeManager.TEXT_SECONDARY);
                noPosts.setAlignmentX(Component.LEFT_ALIGNMENT);
                content.add(noPosts);
            } else {
                content.add(blogRow);
            }
        } catch (SQLException e) {
            System.err.println("Error loading posts: " + e.getMessage());
        }

        // ===== ABOUT SECTION =====
        content.add(Box.createVerticalStrut(30));

        JPanel aboutCard = ThemeManager.createCard();
        aboutCard.setLayout(new BoxLayout(aboutCard, BoxLayout.Y_AXIS));
        aboutCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        aboutCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, ThemeManager.PRIMARY),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
            )
        ));

        JLabel aboutTitle = new JLabel("About the Club");
        aboutTitle.setFont(ThemeManager.FONT_HEADING);
        aboutTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        aboutTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        aboutCard.add(aboutTitle);
        aboutCard.add(Box.createVerticalStrut(10));

        JTextArea aboutText = new JTextArea(
            "The TUK Ability Club is a student-led organization at the Technical University of Kenya dedicated to " +
            "supporting and empowering students with disabilities. We provide a platform for advocacy, social support, " +
            "academic assistance, and community building. Our mission is to create an inclusive environment where every " +
            "student can thrive regardless of ability."
        );
        aboutText.setFont(ThemeManager.FONT_BODY);
        aboutText.setForeground(ThemeManager.TEXT_SECONDARY);
        aboutText.setLineWrap(true);
        aboutText.setWrapStyleWord(true);
        aboutText.setEditable(false);
        aboutText.setBackground(ThemeManager.BG_CARD);
        aboutText.setBorder(null);
        aboutText.setAlignmentX(Component.LEFT_ALIGNMENT);
        aboutCard.add(aboutText);

        content.add(aboutCard);

        // ===== FOOTER =====
        content.add(Box.createVerticalStrut(30));

        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(new Color(17, 24, 39));
        footer.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel footerText = new JLabel("TUK Ability Club Portal \u00A9 2026  |  Technical University of Kenya");
        footerText.setFont(ThemeManager.FONT_SMALL);
        footerText.setForeground(ThemeManager.TEXT_LIGHT);
        footerText.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(footerText);

        footer.add(Box.createVerticalStrut(6));

        JLabel footerText2 = new JLabel("Built by The Three Muskateers — Margaret, Emmanuel, Terry");
        footerText2.setFont(ThemeManager.FONT_SMALL);
        footerText2.setForeground(ThemeManager.TEXT_LIGHT);
        footerText2.setAlignmentX(Component.CENTER_ALIGNMENT);
        footer.add(footerText2);

        content.add(footer);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createEventCard(String title, String date, String time, String location) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(8));

        JLabel dateLabel = new JLabel("\uD83D\uDCC5  " + (date != null ? date : "TBD"));
        dateLabel.setFont(ThemeManager.FONT_SMALL);
        dateLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(dateLabel);

        card.add(Box.createVerticalStrut(4));

        JLabel timeLabel = new JLabel("\uD83D\uDD52  " + (time != null ? time : "TBD"));
        timeLabel.setFont(ThemeManager.FONT_SMALL);
        timeLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(timeLabel);

        card.add(Box.createVerticalStrut(4));

        JLabel locLabel = new JLabel("\uD83D\uDCCD  " + (location != null ? location : "TBD"));
        locLabel.setFont(ThemeManager.FONT_SMALL);
        locLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        locLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(locLabel);

        return card;
    }

    private JPanel createBlogCard(String title, String category, String author, String preview) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Category badge
        JLabel badge = new JLabel("  " + (category != null ? category.toUpperCase() : "GENERAL") + "  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(ThemeManager.TEXT_WHITE);
        badge.setOpaque(true);
        badge.setBackground(ThemeManager.PRIMARY);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(badge);

        card.add(Box.createVerticalStrut(8));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(6));

        JTextArea previewText = new JTextArea(preview);
        previewText.setFont(ThemeManager.FONT_SMALL);
        previewText.setForeground(ThemeManager.TEXT_SECONDARY);
        previewText.setLineWrap(true);
        previewText.setWrapStyleWord(true);
        previewText.setEditable(false);
        previewText.setBackground(ThemeManager.BG_CARD);
        previewText.setBorder(null);
        previewText.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(previewText);

        card.add(Box.createVerticalStrut(6));

        JLabel authorLabel = new JLabel("By " + author);
        authorLabel.setFont(ThemeManager.FONT_SMALL);
        authorLabel.setForeground(ThemeManager.TEXT_LIGHT);
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(authorLabel);

        return card;
    }

    private int getCount(String sql) {
        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return 0;
    }
}