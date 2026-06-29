package com.tukac.controller;

import com.tukac.db.Database;
import com.tukac.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    @GetMapping
    public ResponseEntity<?> getFinance(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("message", "Admin access required"));
        }

        String txSql = "SELECT id, type, description, amount, category, transaction_date FROM transactions ORDER BY transaction_date DESC";
        String sumSql = """
            SELECT
                SUM(CASE WHEN LOWER(type)='income' THEN amount ELSE 0 END) AS total_income,
                SUM(CASE WHEN LOWER(type)='expense' THEN amount ELSE 0 END) AS total_expenses
            FROM transactions
        """;

        List<Map<String, Object>> transactions = new ArrayList<>();
        double totalIncome = 0, totalExpenses = 0;

        try (Connection conn = Database.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sumSql);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalIncome = rs.getDouble("total_income");
                    totalExpenses = rs.getDouble("total_expenses");
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(txSql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> tx = new LinkedHashMap<>();
                    tx.put("id", rs.getLong("id"));
                    tx.put("type", rs.getString("type").toUpperCase());
                    tx.put("description", rs.getString("description"));
                    tx.put("amount", rs.getDouble("amount"));
                    tx.put("category", rs.getString("category"));
                    tx.put("date", rs.getString("transaction_date"));
                    transactions.add(tx);
                }
            }

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("totalExpenses", totalExpenses);
        result.put("balance", totalIncome - totalExpenses);
        result.put("transactions", transactions);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> addTransaction(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) {
            return ResponseEntity.status(403).body(Map.of("message", "Admin access required"));
        }

        String type = (String) body.get("type");
        String description = (String) body.get("description");
        String date = (String) body.get("date");
        String category = (String) body.getOrDefault("category", "General");
        double amount;
        try {
            amount = Double.parseDouble(body.get("amount").toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid amount"));
        }

        if (type == null || description == null || date == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Type, description and date are required"));
        }

        String sql = "INSERT INTO transactions (type, description, amount, category, transaction_date, created_by) VALUES (?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.toLowerCase());
            stmt.setString(2, description);
            stmt.setDouble(3, amount);
            stmt.setString(4, category);
            stmt.setString(5, date);
            stmt.setLong(6, currentUser.getId());
            stmt.executeUpdate();

            return ResponseEntity.ok(Map.of("message", "Transaction added successfully"));

        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Database error: " + e.getMessage()));
        }
    }
}
