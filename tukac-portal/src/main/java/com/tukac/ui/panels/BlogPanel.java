package com.tukac.ui.panels;

import com.tukac.db.Database;
import com.tukac.models.User;
import com.tukac.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BlogPanel extends JPanel {
    private User currentUser;
    private JPanel postsContainer;
    private JScrollPane scrollPane;

    public BlogPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 15));
        setBackground(ThemeManager.BG_MAIN);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.BG_MAIN);

        JLabel title = new JLabel("Club Blog");
        title.setFont(ThemeManager.FONT_HEADING);
        title.setForeground(ThemeManager.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // Posts container
        postsContainer = new JPanel();
        postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
        postsContainer.setBackground(ThemeManager.BG_MAIN);

        scrollPane = new JScrollPane(postsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(ThemeManager.BG_MAIN);
        add(scrollPane, BorderLayout.CENTER);

        loadPosts();
    }

    private void loadPosts() {
        postsContainer.removeAll();

        try {
            Statement stmt = Database.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("""
                SELECT bp.*, u.name as author_name,
                    (SELECT COUNT(*) FROM comments WHERE post_id = bp.id) as comment_count
                FROM blog_posts bp
                LEFT JOIN users u ON bp.author_id = u.id
                ORDER BY bp.published_at DESC
            """);

            boolean hasPosts = false;
            while (rs.next()) {
                hasPosts = true;
                int postId = rs.getInt("id");
                String postTitle = rs.getString("title");
                String body = rs.getString("body");
                String category = rs.getString("category");
                String author = rs.getString("author_name");
                String date = rs.getString("published_at");
                int commentCount = rs.getInt("comment_count");

                if (author == null) author = "Unknown";
                if (category == null) category = "General";

                // Truncate body for preview
                String preview = body.length() > 200 ? body.substring(0, 200) + "..." : body;

                JPanel postCard = createPostCard(postId, postTitle, preview, body, category, author, date, commentCount);
                postsContainer.add(postCard);
                postsContainer.add(Box.createVerticalStrut(15));
            }

            if (!hasPosts) {
                JLabel empty = new JLabel("No blog posts yet. Check back soon!");
                empty.setFont(ThemeManager.FONT_BODY);
                empty.setForeground(ThemeManager.TEXT_SECONDARY);
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                postsContainer.add(empty);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading posts: " + e.getMessage());
        }

        postsContainer.revalidate();
        postsContainer.repaint();
    }

    private JPanel createPostCard(int postId, String title, String preview, String fullBody,
                                   String category, String author, String date, int commentCount) {
        JPanel card = ThemeManager.createCard();
        card.setLayout(new BorderLayout(10, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Top section: category badge + date
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(ThemeManager.BG_CARD);

        JLabel categoryBadge = new JLabel("  " + category.toUpperCase() + "  ");
        categoryBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        categoryBadge.setForeground(ThemeManager.TEXT_WHITE);
        categoryBadge.setOpaque(true);
        categoryBadge.setBackground(ThemeManager.PRIMARY);
        categoryBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        topRow.add(categoryBadge, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(ThemeManager.FONT_SMALL);
        dateLabel.setForeground(ThemeManager.TEXT_LIGHT);
        topRow.add(dateLabel, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);

        // Center: title + preview
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(ThemeManager.BG_CARD);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_SUBHEADING);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(titleLabel);

        centerPanel.add(Box.createVerticalStrut(8));

        JTextArea previewText = new JTextArea(preview);
        previewText.setFont(ThemeManager.FONT_BODY);
        previewText.setForeground(ThemeManager.TEXT_SECONDARY);
        previewText.setLineWrap(true);
        previewText.setWrapStyleWord(true);
        previewText.setEditable(false);
        previewText.setBackground(ThemeManager.BG_CARD);
        previewText.setBorder(null);
        previewText.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(previewText);

        card.add(centerPanel, BorderLayout.CENTER);

        // Bottom: author + read more + comments
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setBackground(ThemeManager.BG_CARD);

        JLabel authorLabel = new JLabel("By " + author);
        authorLabel.setFont(ThemeManager.FONT_SMALL);
        authorLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        bottomRow.add(authorLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(ThemeManager.BG_CARD);

        JLabel commentLabel = new JLabel(commentCount + " comments");
        commentLabel.setFont(ThemeManager.FONT_SMALL);
        commentLabel.setForeground(ThemeManager.TEXT_LIGHT);
        rightPanel.add(commentLabel);

        JButton readMoreBtn = ThemeManager.createLinkButton("Read & Comment");
        readMoreBtn.addActionListener(e -> showFullPost(postId, title, fullBody, category, author, date));
        rightPanel.add(readMoreBtn);

        bottomRow.add(rightPanel, BorderLayout.EAST);
        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    private void showFullPost(int postId, String title, String body, String category, String author, String date) {
        removeAll();
        setLayout(new BorderLayout(0, 15));

        // Back button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ThemeManager.BG_MAIN);

        JButton backBtn = ThemeManager.createButton("\u2190 Back to Blog", new Color(107, 114, 128));
        backBtn.addActionListener(e -> {
            removeAll();
            setLayout(new BorderLayout(0, 15));

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(ThemeManager.BG_MAIN);
            JLabel headerTitle = new JLabel("Club Blog");
            headerTitle.setFont(ThemeManager.FONT_HEADING);
            headerTitle.setForeground(ThemeManager.TEXT_PRIMARY);
            header.add(headerTitle, BorderLayout.WEST);
            add(header, BorderLayout.NORTH);

            postsContainer = new JPanel();
            postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
            postsContainer.setBackground(ThemeManager.BG_MAIN);
            scrollPane = new JScrollPane(postsContainer);
            scrollPane.setBorder(null);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            add(scrollPane, BorderLayout.CENTER);

            loadPosts();
            revalidate();
            repaint();
        });
        topPanel.add(backBtn, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // Post content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ThemeManager.BG_MAIN);

        // Post card
        JPanel postCard = ThemeManager.createCard();
        postCard.setLayout(new BoxLayout(postCard, BoxLayout.Y_AXIS));
        postCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel categoryBadge = new JLabel("  " + category.toUpperCase() + "  ");
        categoryBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        categoryBadge.setForeground(ThemeManager.TEXT_WHITE);
        categoryBadge.setOpaque(true);
        categoryBadge.setBackground(ThemeManager.PRIMARY);
        categoryBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        postCard.add(categoryBadge);
        postCard.add(Box.createVerticalStrut(12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        postCard.add(titleLabel);

        postCard.add(Box.createVerticalStrut(5));

        JLabel metaLabel = new JLabel("By " + author + "  \u2022  " + date);
        metaLabel.setFont(ThemeManager.FONT_SMALL);
        metaLabel.setForeground(ThemeManager.TEXT_LIGHT);
        metaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        postCard.add(metaLabel);

        postCard.add(Box.createVerticalStrut(15));

        JTextArea bodyText = new JTextArea(body);
        bodyText.setFont(ThemeManager.FONT_BODY);
        bodyText.setForeground(ThemeManager.TEXT_PRIMARY);
        bodyText.setLineWrap(true);
        bodyText.setWrapStyleWord(true);
        bodyText.setEditable(false);
        bodyText.setBackground(ThemeManager.BG_CARD);
        bodyText.setBorder(null);
        bodyText.setAlignmentX(Component.LEFT_ALIGNMENT);
        postCard.add(bodyText);

        contentPanel.add(postCard);
        contentPanel.add(Box.createVerticalStrut(20));

        // Comments section
        JLabel commentsHeader = new JLabel("Comments");
        commentsHeader.setFont(ThemeManager.FONT_SUBHEADING);
        commentsHeader.setForeground(ThemeManager.TEXT_PRIMARY);
        commentsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(commentsHeader);
        contentPanel.add(Box.createVerticalStrut(10));

        // Load existing comments
        try {
            PreparedStatement pstmt = Database.getConnection().prepareStatement("""
                SELECT c.*, u.name as commenter_name
                FROM comments c
                LEFT JOIN users u ON c.user_id = u.id
                WHERE c.post_id = ?
                ORDER BY c.created_at ASC
            """);
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();

            boolean hasComments = false;
            while (rs.next()) {
                hasComments = true;
                String commenter = rs.getString("commenter_name");
                String message = rs.getString("message");
                String commentDate = rs.getString("created_at");
                if (commenter == null) commenter = "Unknown";

                JPanel commentCard = new JPanel();
                commentCard.setLayout(new BoxLayout(commentCard, BoxLayout.Y_AXIS));
                commentCard.setBackground(new Color(243, 244, 246));
                commentCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
                commentCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                commentCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                JLabel commenterLabel = new JLabel(commenter + "  \u2022  " + commentDate);
                commenterLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                commenterLabel.setForeground(ThemeManager.TEXT_PRIMARY);
                commenterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                commentCard.add(commenterLabel);

                commentCard.add(Box.createVerticalStrut(5));

                JLabel messageLabel = new JLabel(message);
                messageLabel.setFont(ThemeManager.FONT_BODY);
                messageLabel.setForeground(ThemeManager.TEXT_SECONDARY);
                messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                commentCard.add(messageLabel);

                contentPanel.add(commentCard);
                contentPanel.add(Box.createVerticalStrut(8));
            }

            if (!hasComments) {
                JLabel noComments = new JLabel("No comments yet. Be the first!");
                noComments.setFont(ThemeManager.FONT_BODY);
                noComments.setForeground(ThemeManager.TEXT_LIGHT);
                noComments.setAlignmentX(Component.LEFT_ALIGNMENT);
                contentPanel.add(noComments);
                contentPanel.add(Box.createVerticalStrut(10));
            }

        } catch (SQLException e) {
            System.err.println("Error loading comments: " + e.getMessage());
        }

        // Add comment form
        contentPanel.add(Box.createVerticalStrut(10));

        JLabel addCommentLabel = new JLabel("Add a Comment");
        addCommentLabel.setFont(ThemeManager.FONT_SUBHEADING);
        addCommentLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        addCommentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(addCommentLabel);
        contentPanel.add(Box.createVerticalStrut(8));

        JTextArea commentInput = new JTextArea(3, 40);
        commentInput.setFont(ThemeManager.FONT_BODY);
        commentInput.setLineWrap(true);
        commentInput.setWrapStyleWord(true);
        commentInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        commentInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        commentInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        contentPanel.add(commentInput);
        contentPanel.add(Box.createVerticalStrut(10));

        JButton submitComment = ThemeManager.createButton("Post Comment", ThemeManager.PRIMARY);
        submitComment.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitComment.addActionListener(e -> {
            String message = commentInput.getText().trim();
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please write a comment.");
                return;
            }
            try {
                PreparedStatement pstmt = Database.getConnection().prepareStatement(
                    "INSERT INTO comments (post_id, user_id, message) VALUES (?, ?, ?)"
                );
                pstmt.setInt(1, postId);
                pstmt.setInt(2, currentUser.getId());
                pstmt.setString(3, message);
                pstmt.executeUpdate();

                // Refresh the post view
                showFullPost(postId, title, body, category, author, date);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        contentPanel.add(submitComment);

        JScrollPane postScroll = new JScrollPane(contentPanel);
        postScroll.setBorder(null);
        postScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(postScroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}