package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.User;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('CHAIRPERSON')")
public class UserController {

    @Autowired private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("name", u.getName());
            map.put("studentId", u.getStudentId());
            map.put("email", u.getEmail());
            map.put("role", u.getRole());
            map.put("isApproved", u.getIsApproved());
            map.put("contact", u.getContact());
            map.put("createdAt", u.getCreatedAt());
            map.put("hasDisability", u.isHasDisability());
            map.put("disabilityType", u.getDisabilityType());
            map.put("ncpwdNumber", u.getNcpwdNumber());
            map.put("passportPhoto", u.getPassportPhoto());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approveUser(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User user = opt.get();
        user.setIsApproved(1);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("User approved successfully", null));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<String>> rejectUser(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User user = opt.get();
        user.setIsApproved(0);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("User rejected", null));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<ApiResponse<String>> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newRole = body.get("role");
        List<String> validRoles = List.of("chairperson", "vice-chairperson", "secretary", "treasurer", "member");
        if (newRole == null || !validRoles.contains(newRole)) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("Invalid role. Must be one of: " + String.join(", ", validRoles))
            );
        }
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User user = opt.get();
        user.setRole(newRole);
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.ok("Role updated to " + newRole, null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ResponseEntity.notFound().build();
        userRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted", null));
    }
}
