package com.tukac.controller;

import com.tukac.db.Database;
import com.tukac.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @GetMapping
    public ResponseEntity<?> getMembers(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("message", "Admin access required"));
        }

        String sql = "SELECT id, name, email, role, is_approved, student_id, created_at FROM users ORDER BY created_at DESC";
        List<Map<String, Object>> members = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("id", rs.getLong("id"));
                m.put("name", rs.getString("name"));
                m.put("email", rs.getString("email"));
                m.put("role", rs.getString("role"));
                m.put("studentId", rs.getString("student_id"));
                m.put("approvalStatus", rs.getInt("is_approved") == 1 ? "APPROVED" : "PENDING");
                m.put("joinedAt", rs.getString("created_at"));
                members.add(m);
            }

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("members", members));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveMember(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("message", "Admin access required"));
        }

        String sql = "UPDATE users SET is_approved = 1 WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                return ResponseEntity.status(404).body(Map.of("message", "Member not found"));
            }
            return ResponseEntity.ok(Map.of("message", "Member approved successfully"));

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("message", "Admin access required"));
        }
        if (id.equals(currentUser.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot remove yourself"));
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Member removed"));

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }
    }
}
