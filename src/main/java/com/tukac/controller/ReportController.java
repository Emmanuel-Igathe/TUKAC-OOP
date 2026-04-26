package com.tukac.controller;

import com.tukac.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private UserRepository userRepo;
    @Autowired private EventRepository eventRepo;
    @Autowired private TransactionRepository transRepo;
    @Autowired private BlogPostRepository blogRepo;
    @Autowired private EventRegistrationRepository regRepo;

    @GetMapping
    public ResponseEntity<?> getReports() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMembers", userRepo.count());
        stats.put("approvedMembers", userRepo.findByApprovalStatus("APPROVED").size());
        stats.put("pendingMembers", userRepo.findByApprovalStatus("PENDING").size());
        stats.put("totalEvents", eventRepo.count());
        stats.put("totalPosts", blogRepo.count());
        stats.put("totalRegistrations", regRepo.count());

        BigDecimal income = transRepo.sumByType("INCOME");
        BigDecimal expense = transRepo.sumByType("EXPENSE");
        stats.put("totalIncome", income);
        stats.put("totalExpense", expense);
        stats.put("balance", income.subtract(expense));
        
        stats.put("recentTransactions", transRepo.findAllByOrderByDateDesc().stream().limit(5).toList());
        return ResponseEntity.ok(stats);
    }
}
