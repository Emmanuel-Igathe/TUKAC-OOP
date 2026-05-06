package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.tukac.ui.ThemeManager;

public class SiteWhyJoinPage extends JPanel {
    public SiteWhyJoinPage() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);

        // Banner
        JPanel banner = new JPanel(); banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS)); banner.setBackground(new Color(88,28,135));
        banner.setBorder(BorderFactory.createEmptyBorder(35,50,35,50)); banner.setMaximumSize(new Dimension(Integer.MAX_VALUE,130));
        JLabel h1 = new JLabel("Why Join TUK Ability Club?"); h1.setFont(new Font("Segoe UI",Font.BOLD,26)); h1.setForeground(Color.WHITE); h1.setAlignmentX(Component.LEFT_ALIGNMENT); banner.add(h1);
        banner.add(Box.createVerticalStrut(6));
        JLabel h2 = new JLabel("Discover the benefits of being part of our community"); h2.setFont(new Font("Segoe UI",Font.PLAIN,13)); h2.setForeground(new Color(216,180,254)); h2.setAlignmentX(Component.LEFT_ALIGNMENT); banner.add(h2);
        page.add(banner);
        page.add(Box.createVerticalStrut(25));

        String[][] benefits = {
            {"Community & Belonging", "Join a supportive family of students who understand your journey. Build lifelong friendships in a safe, welcoming space."},
            {"Advocacy & Representation", "Your voice matters. We advocate for accessible facilities, inclusive policies, and equal opportunities at TUK and beyond."},
            {"Skills Development", "Attend workshops on leadership, technology, communication, and career readiness. Build skills that set you apart."},
            {"Events & Social Life", "From awareness weeks to sports days, talent shows to study groups — there's always something happening."},
            {"Academic Support", "Get peer tutoring, study groups, and academic mentorship from fellow members and university staff."},
            {"Networking", "Connect with alumni, professionals, and partner organizations. Open doors for internships and career opportunities."},
        };
        Color[] colors = {ThemeManager.PRIMARY, ThemeManager.PURPLE, ThemeManager.SUCCESS, new Color(234,179,8), new Color(6,182,212), ThemeManager.DANGER};

        JPanel grid = new JPanel(new GridLayout(3, 2, 15, 15));
        grid.setBackground(ThemeManager.BG_MAIN);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 480));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        for (int i = 0; i < benefits.length; i++) {
            JPanel card = ThemeManager.createCard(); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(3,0,0,0,colors[i]),
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeManager.BORDER,1), BorderFactory.createEmptyBorder(18,18,18,18))));
            JLabel t = new JLabel(benefits[i][0]); t.setFont(new Font("Segoe UI",Font.BOLD,15)); t.setForeground(ThemeManager.TEXT_PRIMARY); t.setAlignmentX(Component.LEFT_ALIGNMENT); card.add(t);
            card.add(Box.createVerticalStrut(8));
            JTextArea d = new JTextArea(benefits[i][1]); d.setFont(ThemeManager.FONT_BODY); d.setForeground(ThemeManager.TEXT_SECONDARY); d.setLineWrap(true); d.setWrapStyleWord(true);
            d.setEditable(false); d.setBackground(ThemeManager.BG_CARD); d.setBorder(null); d.setAlignmentX(Component.LEFT_ALIGNMENT); card.add(d);
            grid.add(card);
        }
        page.add(grid);
        page.add(Box.createVerticalStrut(30));
        page.add(MainFrame.createFooter());

        JScrollPane scroll = new JScrollPane(page); scroll.setBorder(null); scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }
}