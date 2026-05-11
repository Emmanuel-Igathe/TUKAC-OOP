package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.Transaction;
import com.tukac.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired private TransactionRepository transactionRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransactions() {
        List<Transaction> transactions = transactionRepository.findAllByOrderByTransactionDateDesc();
        Double balance = transactionRepository.calculateBalance();

        double income = transactions.stream()
                .filter(t -> "income".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount).sum();
        double expense = transactions.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount).sum();

        Map<String, Object> result = new HashMap<>();
        result.put("transactions", transactions);
        result.put("balance", balance != null ? balance : 0.0);
        result.put("totalIncome", income);
        result.put("totalExpense", expense);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','TREASURER')")
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(
            @RequestBody Transaction transaction, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        transaction.setCreatedBy(userId);
        Transaction saved = transactionRepository.save(transaction);
        return ResponseEntity.ok(ApiResponse.ok("Transaction recorded", saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','TREASURER')")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable Long id) {
        if (!transactionRepository.existsById(id)) return ResponseEntity.notFound().build();
        transactionRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Transaction deleted", null));
    }
}
