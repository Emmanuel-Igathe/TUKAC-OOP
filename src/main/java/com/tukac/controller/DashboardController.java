package com.tukac.controller;

import com.tukac.model.User;
import com.tukac.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private UserRepository userRepo;
    @Autowired private EventRepository eventRepo;
    @Autowired private BlogPostRepository blogRepo;
    @Autowired private TransactionRepository transRepo;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(@AuthenticationPrincipal UserDetails principal) {
        User currentUser = userRepo.findByEmail(principal.getUsername()).orElseThrow();

        Map<String, Object> stats = new HashMap<>();
        stats.put("currentUser", currentUser);
        stats.put("totalMembers", userRepo.count());
        stats.put("totalEvents", eventRepo.count());
        stats.put("totalPosts", blogRepo.count());
        stats.put("upcomingEvents", eventRepo.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now()).stream().limit(3).toList());
        stats.put("recentPosts", blogRepo.findAllByOrderByCreatedAtDesc().stream().limit(3).toList());
        stats.put("pendingMembers", userRepo.findByApprovalStatus("PENDING").size());

        java.math.BigDecimal income = transRepo.sumByType("INCOME");
        java.math.BigDecimal expense = transRepo.sumByType("EXPENSE");
        stats.put("balance", income.subtract(expense));

        return ResponseEntity.ok(stats);
    }
}
