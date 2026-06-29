package com.tukac.controller;

import com.tukac.auth.TokenStore;
import com.tukac.db.Database;
import com.tukac.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final TokenStore tokenStore;

    public AuthController(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }

        String sql = "SELECT id, name, student_id, email, role, is_approved FROM users WHERE email = ? AND password = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
            }

            if (rs.getInt("is_approved") == 0) {
                return ResponseEntity.status(403).body(Map.of("message", "Your account is pending admin approval"));
            }

            User user = new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("student_id"),
                rs.getString("email"),
                rs.getString("role")
            );
            user.setIsApproved(rs.getInt("is_approved"));

            String token = tokenStore.createToken(user);

            // Map DB role to web role: chairperson -> admin so isAdmin() in JS works
            String webRole = "chairperson".equals(user.getRole()) ? "admin" : user.getRole();

            return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", webRole,
                    "studentId", user.getStudentId() != null ? user.getStudentId() : ""
                )
            ));

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");
        String confirmPassword = body.get("confirmPassword");
        String studentId = body.get("studentId");

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Full name is required"));
        }
        if (email == null || !email.contains("@")) {
            return ResponseEntity.badRequest().body(Map.of("message", "A valid email is required"));
        }
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters"));
        }
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match"));
        }
        if (studentId == null || studentId.isBlank()) {
            // Auto-generate from email prefix
            studentId = "TUK-" + email.split("@")[0].toUpperCase();
        }

        String sql = "INSERT INTO users (name, student_id, email, password, role, is_approved) VALUES (?,?,?,?,'member',0)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name.trim());
            stmt.setString(2, studentId.trim());
            stmt.setString(3, email.trim().toLowerCase());
            stmt.setString(4, password);
            stmt.executeUpdate();

            return ResponseEntity.ok(Map.of("message",
                "Registration successful! Your account is pending admin approval. You will be notified once approved."));

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email or Student ID is already registered"));
            }
            return ResponseEntity.internalServerError().body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }
}
