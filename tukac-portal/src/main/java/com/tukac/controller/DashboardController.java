package com.tukac.controller;

import com.tukac.db.Database;
import com.tukac.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Login required"));
        }

        try (Connection conn = Database.getConnection()) {
            long totalMembers = queryCount(conn, "SELECT COUNT(*) FROM users WHERE is_approved = 1");
            long totalEvents = queryCount(conn, "SELECT COUNT(*) FROM events");
            long totalPosts = queryCount(conn, "SELECT COUNT(*) FROM blog_posts");
            double income = querySum(conn, "SELECT SUM(amount) FROM transactions WHERE LOWER(type)='income'");
            double expenses = querySum(conn, "SELECT SUM(amount) FROM transactions WHERE LOWER(type)='expense'");

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalMembers", totalMembers);
            stats.put("totalEvents", totalEvents);
            stats.put("totalPosts", totalPosts);
            stats.put("balance", income - expenses);
            return ResponseEntity.ok(stats);

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }
    }

    private long queryCount(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    private double querySum(Connection conn, String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        }
    }
}
