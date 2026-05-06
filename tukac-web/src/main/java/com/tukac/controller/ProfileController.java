package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.User;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProfile(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        User u = opt.get();
        Map<String, Object> map = new HashMap<>();
        map.put("name", u.getName());
        map.put("studentId", u.getStudentId());
        map.put("email", u.getEmail());
        map.put("contact", u.getContact());
        map.put("hasDisability", u.isHasDisability());
        map.put("disabilityType", u.getDisabilityType());
        map.put("ncpwdNumber", u.getNcpwdNumber());
        map.put("passportPhoto", u.getPassportPhoto());
        map.put("role", u.getRole());

        return ResponseEntity.ok(ApiResponse.ok(map));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateProfile(
            Authentication auth, @RequestBody Map<String, Object> payload) {
        Long userId = (Long) auth.getCredentials();
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        User u = opt.get();
        
        if (payload.containsKey("name")) u.setName((String) payload.get("name"));
        if (payload.containsKey("email")) u.setEmail((String) payload.get("email"));
        if (payload.containsKey("contact")) u.setContact((String) payload.get("contact"));
        
        if (payload.containsKey("hasDisability")) {
            Boolean hasDis = (Boolean) payload.get("hasDisability");
            u.setHasDisability(hasDis);
            if (hasDis != null && hasDis) {
                u.setDisabilityType((String) payload.get("disabilityType"));
                u.setNcpwdNumber((String) payload.get("ncpwdNumber"));
            } else {
                u.setDisabilityType(null);
                u.setNcpwdNumber(null);
            }
        }
        
        if (payload.containsKey("passportPhoto")) {
            u.setPassportPhoto((String) payload.get("passportPhoto"));
        }

        userRepository.save(u);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", null));
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            Authentication auth, @RequestBody Map<String, String> payload) {
        Long userId = (Long) auth.getCredentials();
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        String currentPassword = payload.get("currentPassword");
        String newPassword = payload.get("newPassword");

        if (currentPassword == null || newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid password input"));
        }

        User u = opt.get();
        boolean matches = false;
        if (u.getPassword().startsWith("$2a$") || u.getPassword().startsWith("$2b$")) {
            matches = passwordEncoder.matches(currentPassword, u.getPassword());
        } else {
            matches = u.getPassword().equals(currentPassword);
        }

        if (!matches) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Current password is incorrect"));
        }

        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
        return ResponseEntity.ok(ApiResponse.ok("Password updated successfully", null));
    }
    @Autowired
    private com.tukac.repository.TransactionRepository transactionRepository;

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<java.util.List<com.tukac.model.Transaction>>> getMyTransactions(Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        java.util.List<com.tukac.model.Transaction> txs = transactionRepository.findByCreatedByOrderByTransactionDateDesc(userId);
        return ResponseEntity.ok(ApiResponse.ok(txs));
    }
}
