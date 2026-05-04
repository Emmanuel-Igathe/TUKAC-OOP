package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(ThemeManager.BG_MAIN);

        JLabel header = new JLabel("About TUK Ability Club");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ThemeManager.BG_MAIN);

        // Mission
        content.add(createInfoCard("Our Mission",
            "To empower students with disabilities at the Technical University of Kenya through advocacy, support, community building, and inclusive opportunities that enable every student to achieve their full potential.",
            ThemeManager.PRIMARY));
        content.add(Box.createVerticalStrut(15));

        // Vision
        content.add(createInfoCard("Our Vision",
            "An inclusive university environment where every student thrives regardless of ability — where barriers are removed, voices are heard, and diversity is celebrated as strength.",
            ThemeManager.PURPLE));
        content.add(Box.createVerticalStrut(15));

        // Objectives
        content.add(createInfoCard("Our Objectives",
            "1. Promote disability awareness and sensitivity across the university community\n" +
            "2. Provide academic and social support for students with disabilities\n" +
            "3. Organize inclusive events, workshops, and activities\n" +
            "4. Advocate for accessible facilities, materials, and policies\n" +
            "5. Foster a network of support among students, staff, and partners",
            ThemeManager.SUCCESS));
        content.add(Box.createVerticalStrut(15));

        // Leadership
        JPanel leaderCard = ThemeManager.createCard();
        leaderCard.setLayout(new BoxLayout(leaderCard, BoxLayout.Y_AXIS));
        leaderCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        leaderCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, new Color(234, 179, 8)),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        ));

        JLabel leaderTitle = new JLabel("Leadership Team");
        leaderTitle.setFont(ThemeManager.FONT_SUBHEADING);
        leaderTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        leaderTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leaderCard.add(leaderTitle);
        leaderCard.add(Box.createVerticalStrut(12));

        JPanel leadersRow = new JPanel(new GridLayout(1, 3, 15, 0));
        leadersRow.setBackground(ThemeManager.BG_CARD);
        leadersRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        leadersRow.add(createLeaderItem("Chairperson", "Margaret Wambui Nduta", ThemeManager.PRIMARY));
        leadersRow.add(createLeaderItem("Vice Chairperson", "Mararo Emmanuel Igathe", ThemeManager.PURPLE));
        leadersRow.add(createLeaderItem("Secretary", "Terry Mwikali", ThemeManager.SUCCESS));

        leaderCard.add(leadersRow);
        content.add(leaderCard);
        content.add(Box.createVerticalStrut(15));

        // Contact
        content.add(createInfoCard("Contact Us",
            "Email: abilityclub@tuk.ac.ke\n" +
            "Office: Student Center, Room 105\n" +
            "Phone: +254 700 000 000\n" +
            "Meeting: Every Wednesday, 2:00 PM - 4:00 PM",
            new Color(234, 179, 8)));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createInfoCard(String title, String body, Color accentColor) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
            )
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_SUBHEADING);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));

        JTextArea bodyText = new JTextArea(body);
        bodyText.setFont(ThemeManager.FONT_BODY);
        bodyText.setForeground(ThemeManager.TEXT_SECONDARY);
        bodyText.setLineWrap(true);
        bodyText.setWrapStyleWord(true);
        bodyText.setEditable(false);
        bodyText.setBackground(ThemeManager.BG_CARD);
        bodyText.setBorder(null);
        bodyText.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bodyText);

        return card;
    }

    private JPanel createLeaderItem(String role, String name, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.BG_MAIN);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(ThemeManager.FONT_SMALL);
        roleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        panel.add(roleLabel);

        panel.add(Box.createVerticalStrut(4));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(color);
        panel.add(nameLabel);

        return panel;
    }
}