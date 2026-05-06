package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.repository.BlogPostRepository;
import com.tukac.repository.EventRepository;
import com.tukac.repository.TransactionRepository;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired private EventRepository eventRepository;
    @Autowired private BlogPostRepository blogPostRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        long eventCount = eventRepository.count();
        long blogCount = blogPostRepository.count();
        Double balance = transactionRepository.calculateBalance();
        long memberCount = userRepository.count();

        Map<String, Object> stats = Map.of(
            "eventCount", eventCount,
            "blogCount", blogCount,
            "balance", balance != null ? balance : 0.0,
            "memberCount", memberCount
        );

        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
