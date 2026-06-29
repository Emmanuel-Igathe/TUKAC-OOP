package com.tukac.controller;

import com.tukac.db.Database;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    @GetMapping("/public")
    public ResponseEntity<?> getPublicPosts() {
        String sql = """
            SELECT b.id, b.title, b.body, b.category, b.published_at,
                   u.name AS author_name
            FROM blog_posts b
            LEFT JOIN users u ON u.id = b.author_id
            ORDER BY b.published_at DESC
        """;

        List<Map<String, Object>> posts = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> post = new LinkedHashMap<>();
                post.put("id", rs.getLong("id"));
                post.put("title", rs.getString("title"));
                post.put("body", rs.getString("body"));
                post.put("category", rs.getString("category"));
                post.put("publishedAt", rs.getString("published_at"));
                post.put("author", Map.of("name", rs.getString("author_name") != null ? rs.getString("author_name") : "TUKAC Team"));
                posts.add(post);
            }

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }

        return ResponseEntity.ok(posts);
    }
}
