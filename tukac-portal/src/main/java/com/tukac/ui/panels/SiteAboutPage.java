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
import javax.swing.SwingConstants;

import com.tukac.ui.ThemeManager;

public class SiteAboutPage extends JPanel {
    public SiteAboutPage() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.BG_MAIN);

        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBackground(ThemeManager.BG_MAIN);
        page.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Banner
        page.add(createBanner("About TUK Ability Club", "Empowering students with disabilities since 2024", new Color(30, 58, 138)));
        page.add(Box.createVerticalStrut(25));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(ThemeManager.BG_MAIN);
        inner.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        inner.add(infoCard("Our Mission", "To empower students with disabilities at the Technical University of Kenya through advocacy, support, community building, and inclusive opportunities that enable every student to achieve their full potential.", ThemeManager.PRIMARY));
        inner.add(Box.createVerticalStrut(15));
        inner.add(infoCard("Our Vision", "An inclusive university environment where every student thrives regardless of ability — where barriers are removed, voices are heard, and diversity is celebrated as strength.", ThemeManager.PURPLE));
        inner.add(Box.createVerticalStrut(15));
        inner.add(infoCard("Our History", "Founded in 2024 by a group of passionate students, the Ability Club started as a small support group. Today, we are a vibrant community that advocates for disability rights, organizes inclusive events, and provides academic and social support to all members.", ThemeManager.SUCCESS));
        inner.add(Box.createVerticalStrut(25));

        // Leadership
        JLabel lTitle = new JLabel("Leadership Team");
        lTitle.setFont(ThemeManager.FONT_HEADING);
        lTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        lTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(lTitle);
        inner.add(Box.createVerticalStrut(12));

        JPanel leaders = new JPanel(new GridLayout(1, 3, 15, 0));
        leaders.setBackground(ThemeManager.BG_MAIN);
        leaders.setAlignmentX(Component.LEFT_ALIGNMENT);
        leaders.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        leaders.add(leaderCard("Chairperson", "Margaret Wambui Nduta", ThemeManager.PRIMARY));
        leaders.add(leaderCard("Vice Chairperson", "Mararo Emmanuel Igathe", ThemeManager.PURPLE));
        leaders.add(leaderCard("Secretary", "Terry Mwikali", ThemeManager.SUCCESS));
        inner.add(leaders);

        page.add(inner);
        page.add(Box.createVerticalStrut(30));
        page.add(MainFrame.createFooter());

        JScrollPane scroll = new JScrollPane(page);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createBanner(String title, String sub, Color bg) {
        JPanel b = new JPanel(); b.setLayout(new BoxLayout(b, BoxLayout.Y_AXIS)); b.setBackground(bg);
        b.setBorder(BorderFactory.createEmptyBorder(35, 50, 35, 50)); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        JLabel t = new JLabel(title); t.setFont(new Font("Segoe UI", Font.BOLD, 26)); t.setForeground(Color.WHITE); t.setAlignmentX(Component.LEFT_ALIGNMENT); b.add(t);
        b.add(Box.createVerticalStrut(6));
        JLabel s = new JLabel(sub); s.setFont(new Font("Segoe UI", Font.PLAIN, 13)); s.setForeground(new Color(147,197,253)); s.setAlignmentX(Component.LEFT_ALIGNMENT); b.add(s);
        return b;
    }

    private JPanel infoCard(String title, String body, Color accent) {
        JPanel c = ThemeManager.createCard(); c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS)); c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(3,0,0,0,accent),
            BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(ThemeManager.BORDER,1), BorderFactory.createEmptyBorder(20,22,20,22))));
        JLabel t = new JLabel(title); t.setFont(ThemeManager.FONT_SUBHEADING); t.setForeground(ThemeManager.TEXT_PRIMARY); t.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(t);
        c.add(Box.createVerticalStrut(8));
        JTextArea b = new JTextArea(body); b.setFont(ThemeManager.FONT_BODY); b.setForeground(ThemeManager.TEXT_SECONDARY); b.setLineWrap(true); b.setWrapStyleWord(true);
        b.setEditable(false); b.setBackground(ThemeManager.BG_CARD); b.setBorder(null); b.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(b);
        return c;
    }

    private JPanel leaderCard(String role, String name, Color color) {
        JPanel c = ThemeManager.createCard(); c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        String init = ""; for (String p : name.split(" ")) if (!p.isEmpty()) init += p.charAt(0); if (init.length() > 2) init = init.substring(0,2);
        JLabel av = new JLabel(init, SwingConstants.CENTER); av.setPreferredSize(new Dimension(44,44)); av.setMaximumSize(new Dimension(44,44));
        av.setOpaque(true); av.setBackground(color); av.setForeground(Color.WHITE); av.setFont(new Font("Segoe UI",Font.BOLD,16)); av.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(av);
        c.add(Box.createVerticalStrut(8));
        JLabel r = new JLabel(role); r.setFont(ThemeManager.FONT_SMALL); r.setForeground(ThemeManager.TEXT_SECONDARY); r.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(r);
        JLabel n = new JLabel(name); n.setFont(new Font("Segoe UI",Font.BOLD,14)); n.setForeground(color); n.setAlignmentX(Component.LEFT_ALIGNMENT); c.add(n);
        return c;
    }
}