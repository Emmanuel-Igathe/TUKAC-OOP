package com.tukac.ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.tukac.ui.ThemeManager;

public class HelpPanel extends JPanel {

    public HelpPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(ThemeManager.BG_MAIN);

        JLabel header = new JLabel("Help & FAQ");
        header.setFont(ThemeManager.FONT_HEADING);
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        add(header, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(ThemeManager.BG_MAIN);

        String[][] faqs = {
            {"How do I register for the club?",
             "Click 'Register' on the login screen. Fill in your name, student ID, email, and password. After registering, your account must be approved by an admin before you can log in."},
            {"How do I RSVP to an event?",
             "Go to the Events page from the sidebar. Select an event from the table and click the 'RSVP to Event' button. You can cancel your RSVP at any time by selecting the event and clicking 'Cancel RSVP'."},
            {"How do I view the club's financial records?",
             "Click 'Finances' in the sidebar to see the financial overview including total income, expenses, balance, and a full transaction history."},
            {"How do I read and comment on blog posts?",
             "Go to the Blog page from the sidebar. Click 'Read & Comment' on any post to see the full article and leave a comment."},
            {"How do I update my profile?",
             "Click 'My Profile' in the sidebar. You can edit your name, email, and contact number. You can also change your password from the same page."},
            {"What are the different user roles?",
             "Member: Can view events, finances, blog, RSVP, and comment.\nExecutive: Can also manage events, blog posts, and finances.\nAdmin: Can do everything plus approve users and assign roles."},
            {"Why can't I log in after registering?",
             "New accounts require admin approval. Please wait for an admin to approve your account, or contact the club leadership."},
            {"How do I contact the club?",
             "Visit the About page from the sidebar to find contact information including email, office location, and meeting times."},
            {"Who built this portal?",
             "This portal was built by The Three Muskateers — Margaret Wambui Nduta, Mararo Emmanuel Igathe, and Terry Mwikali — for the TUK Ability Club."},
        };

        for (int i = 0; i < faqs.length; i++) {
            content.add(createFaqItem(i + 1, faqs[i][0], faqs[i][1]));
            content.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createFaqItem(int num, String question, String answer) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qLabel = new JLabel(num + ". " + question);
        qLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        qLabel.setForeground(ThemeManager.PRIMARY);
        qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(qLabel);
        card.add(Box.createVerticalStrut(8));

        JTextArea aText = new JTextArea(answer);
        aText.setFont(ThemeManager.FONT_BODY);
        aText.setForeground(ThemeManager.TEXT_SECONDARY);
        aText.setLineWrap(true);
        aText.setWrapStyleWord(true);
        aText.setEditable(false);
        aText.setBackground(ThemeManager.BG_CARD);
        aText.setBorder(null);
        aText.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(aText);

        return card;
    }
}