package com.tukac.controller;

import com.tukac.model.User;
import com.tukac.repository.UserRepository;
import com.tukac.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.get("email"), loginRequest.get("password")));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User user = userRepo.findByEmail(loginRequest.get("email")).orElseThrow();

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("user", user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> signUpRequest) {
        if (userRepo.existsByEmail(signUpRequest.get("email"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error: Email is already in use!"));
        }

        if (signUpRequest.get("studentId") != null && !signUpRequest.get("studentId").isBlank() &&
                userRepo.existsByStudentId(signUpRequest.get("studentId"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error: Student ID is already in use!"));
        }

        if (!signUpRequest.get("password").equals(signUpRequest.get("confirmPassword"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error: Passwords do not match!"));
        }

        User user = new User();
        user.setName(signUpRequest.get("name"));
        user.setEmail(signUpRequest.get("email"));
        user.setStudentId(signUpRequest.get("studentId"));
        user.setContactDetails(signUpRequest.get("contactDetails"));
        user.setPassword(encoder.encode(signUpRequest.get("password")));
        user.setRole("MEMBER");
        user.setApprovalStatus("PENDING");

        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully! Please wait for admin approval."));
    }
}
