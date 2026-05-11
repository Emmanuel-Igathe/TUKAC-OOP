package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.User;
import com.tukac.repository.EventRepository;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPublicStats() {
        long memberCount = userRepository.count();
        long eventCount = eventRepository.count();
        int yearsActive = LocalDate.now().getYear() - 2017;
        if (yearsActive < 1) yearsActive = 1;

        Map<String, Object> stats = Map.of(
            "memberCount", memberCount,
            "eventCount", eventCount,
            "yearsActive", yearsActive
        );
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/executives")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getExecutives() {
        List<String> execRoles = List.of("chairperson", "vice-chairperson", "secretary", "treasurer");
        List<User> executives = userRepository.findByRoleIn(execRoles);

        List<Map<String, String>> result = executives.stream()
            .filter(u -> !"admin@tukac.com".equals(u.getEmail()))  // exclude system seed account
            .sorted((a, b) -> execRoles.indexOf(a.getRole()) - execRoles.indexOf(b.getRole()))
            .map(u -> {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("role", u.getRole());
                m.put("name", u.getName());
                m.put("email", u.getEmail() != null ? u.getEmail() : "");
                m.put("contact", u.getContact() != null ? u.getContact() : "");
                return m;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
