package com.tukac.controller;

import com.tukac.db.Database;
import com.tukac.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @GetMapping("/public")
    public ResponseEntity<?> getPublicEvents() {
        String sql = """
            SELECT e.id, e.title, e.description, e.event_date, e.event_time, e.location, e.capacity,
                   COUNT(r.id) AS rsvp_count
            FROM events e
            LEFT JOIN event_registrations r ON r.event_id = e.id AND r.status = 'registered'
            GROUP BY e.id
            ORDER BY e.event_date ASC
        """;

        List<Map<String, Object>> events = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> event = new LinkedHashMap<>();
                event.put("id", rs.getLong("id"));
                event.put("title", rs.getString("title"));
                event.put("description", rs.getString("description"));
                event.put("date", rs.getString("event_date"));
                event.put("time", rs.getString("event_time"));
                event.put("location", rs.getString("location"));
                event.put("capacity", rs.getInt("capacity"));
                event.put("rsvpCount", rs.getInt("rsvp_count"));
                events.add(event);
            }

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("upcomingEvents", events));
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> rsvpEvent(@PathVariable Long id, HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Login required to RSVP"));
        }

        String sql = "INSERT OR IGNORE INTO event_registrations (user_id, event_id, status) VALUES (?, ?, 'registered')";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user.getId());
            stmt.setLong(2, id);
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                return ResponseEntity.ok(Map.of("message", "You are already registered for this event"));
            }
            return ResponseEntity.ok(Map.of("message", "Successfully registered for the event!"));

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Could not register: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/register")
    public ResponseEntity<?> cancelRsvp(@PathVariable Long id, HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Login required"));
        }

        String sql = "DELETE FROM event_registrations WHERE user_id = ? AND event_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, user.getId());
            stmt.setLong(2, id);
            stmt.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "RSVP cancelled"));

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Could not cancel: " + e.getMessage()));
        }
    }
}
