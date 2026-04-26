package com.tukac.controller;

import com.tukac.model.Transaction;
import com.tukac.model.User;
import com.tukac.repository.TransactionRepository;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    @Autowired private TransactionRepository transRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping
    public ResponseEntity<?> getFinances() {
        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transRepo.findAllByOrderByDateDesc());
        BigDecimal income = transRepo.sumByType("INCOME");
        BigDecimal expense = transRepo.sumByType("EXPENSE");
        response.put("totalIncome", income);
        response.put("totalExpense", expense);
        response.put("balance", income.subtract(expense));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        transaction.setCreatedBy(user);
        Transaction saved = transRepo.save(transaction);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        transRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Transaction deleted"));
    }
}
