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

public class SiteContactPage extends JPanel {
    public SiteContactPage() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);

        // Banner
        JPanel banner = new JPanel(); banner.setLayout(new BoxLayout(banner, BoxLayout.Y_AXIS)); banner.setBackground(new Color(22,101,52));
        banner.setBorder(BorderFactory.createEmptyBorder(35,50,35,50)); banner.setMaximumSize(new Dimension(Integer.MAX_VALUE,130));
        JLabel h1 = new JLabel("Contact Us"); h1.setFont(new Font("Segoe UI",Font.BOLD,26)); h1.setForeground(Color.WHITE); h1.setAlignmentX(Component.LEFT_ALIGNMENT); banner.add(h1);
        banner.add(Box.createVerticalStrut(6));
        JLabel h2 = new JLabel("We'd love to hear from you. Reach out anytime!"); h2.setFont(new Font("Segoe UI",Font.PLAIN,13)); h2.setForeground(new Color(187,247,208)); h2.setAlignmentX(Component.LEFT_ALIGNMENT); banner.add(h2);
        page.add(banner);
        page.add(Box.createVerticalStrut(25));

        JPanel grid = new JPanel(new GridLayout(2, 2, 15, 15));
        grid.setBackground(ThemeManager.BG_MAIN);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        grid.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        grid.add(contactCard("Email", "abilityclub@tuk.ac.ke", "Send us an email anytime", ThemeManager.PRIMARY));
        grid.add(contactCard("Phone", "+254 700 000 000", "Call during office hours", ThemeManager.SUCCESS));
        grid.add(contactCard("Office", "Student Center, Room 105", "Visit us on campus", ThemeManager.PURPLE));
        grid.add(contactCard("Meetings", "Every Wednesday 2-4 PM", "Join our weekly meetings", new Color(234,179,8)));

        page.add(grid);
        page.add(Box.createVerticalStrut(25));

        // Extra info
        JPanel infoCard = ThemeManager.createCard(); infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS)); infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(3,0,0,0,ThemeManager.SUCCESS),
            BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeManager.BORDER,1), BorderFactory.createEmptyBorder(20,22,20,22))));
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        JPanel infoWrap = new JPanel(new BorderLayout()); infoWrap.setBackground(ThemeManager.BG_MAIN);
        infoWrap.setBorder(BorderFactory.createEmptyBorder(0,40,0,40)); infoWrap.add(infoCard, BorderLayout.CENTER);

        JLabel ft = new JLabel("Find Us on Campus"); ft.setFont(ThemeManager.FONT_SUBHEADING); ft.setForeground(ThemeManager.TEXT_PRIMARY); ft.setAlignmentX(Component.LEFT_ALIGNMENT); infoCard.add(ft);
        infoCard.add(Box.createVerticalStrut(8));
        JTextArea fb = new JTextArea("Our office is on the ground floor of the Student Center, Room 105. Fully accessible with ramp access and wide doorways. Open Monday-Friday, 9 AM - 5 PM. Drop by anytime — no appointment needed!\n\nFor urgent matters outside office hours, email abilityclub@tuk.ac.ke and we'll respond within 24 hours.");
        fb.setFont(ThemeManager.FONT_BODY); fb.setForeground(ThemeManager.TEXT_SECONDARY); fb.setLineWrap(true); fb.setWrapStyleWord(true); fb.setEditable(false); fb.setBackground(ThemeManager.BG_CARD); fb.setBorder(null); fb.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(fb);
        page.add(infoWrap);

        page.add(Box.createVerticalStrut(30));
        page.add(MainFrame.createFooter());

        JScrollPane scroll = new JScrollPane(page); scroll.setBorder(null); scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel contactCard(String title, String value, String desc, Color accent) {
        JPanel c = ThemeManager.createCard(); c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(3,0,0,0,accent),
            BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeManager.BORDER,1), BorderFactory.createEmptyBorder(18,18,18,18))));
        JLabel t = new JLabel(title); t.setFont(ThemeManager.FONT_SMALL); t.setForeground(ThemeManager.TEXT_SECONDARY); t.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(t);
        c.add(Box.createVerticalStrut(6));
        JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI",Font.BOLD,15)); v.setForeground(ThemeManager.TEXT_PRIMARY); v.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(v);
        c.add(Box.createVerticalStrut(4));
        JLabel d = new JLabel(desc); d.setFont(ThemeManager.FONT_SMALL); d.setForeground(ThemeManager.TEXT_LIGHT); d.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(d);
        return c;
    }
}