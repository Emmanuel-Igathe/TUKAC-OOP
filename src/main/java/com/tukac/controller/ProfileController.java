package com.tukac.controller;

import com.tukac.model.User;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;

    @GetMapping
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> payload, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        
        if (payload.containsKey("name")) user.setName(payload.get("name"));
        if (payload.containsKey("contactDetails")) user.setContactDetails(payload.get("contactDetails"));
        
        String newPassword = payload.get("newPassword");
        String confirmPassword = payload.get("confirmPassword");
        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
            }
            user.setPassword(encoder.encode(newPassword));
        }
        userRepo.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }
}
