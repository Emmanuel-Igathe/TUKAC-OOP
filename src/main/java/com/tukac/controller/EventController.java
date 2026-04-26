package com.tukac.controller;

import com.tukac.model.Event;
import com.tukac.model.EventRegistration;
import com.tukac.model.User;
import com.tukac.repository.EventRegistrationRepository;
import com.tukac.repository.EventRepository;
import com.tukac.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired private EventRepository eventRepo;
    @Autowired private EventRegistrationRepository regRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/public")
    public ResponseEntity<?> getAllEvents() {
        Map<String, Object> response = new HashMap<>();
        response.put("upcomingEvents", eventRepo.findByDateGreaterThanEqualOrderByDateAsc(LocalDate.now()));
        response.put("pastEvents", eventRepo.findByDateBeforeOrderByDateDesc(LocalDate.now()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getEventDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        Event event = eventRepo.findById(id).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        response.put("event", event);
        
        if (principal != null) {
            User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
            response.put("isRegistered", regRepo.existsByEventAndUser(event, user));
        } else {
            response.put("isRegistered", false);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Event event, @AuthenticationPrincipal UserDetails principal) {
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        event.setCreatedBy(user);
        Event saved = eventRepo.save(event);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> register(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        Event event = eventRepo.findById(id).orElseThrow();
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        
        if (!regRepo.existsByEventAndUser(event, user)) {
            EventRegistration reg = new EventRegistration();
            reg.setEvent(event);
            reg.setUser(user);
            reg.setRegisteredAt(LocalDateTime.now());
            regRepo.save(reg);
            return ResponseEntity.ok(Map.of("message", "Registered successfully"));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Already registered"));
    }

    @PostMapping("/{id}/unregister")
    public ResponseEntity<?> unregister(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        Event event = eventRepo.findById(id).orElseThrow();
        User user = userRepo.findByEmail(principal.getUsername()).orElseThrow();
        regRepo.findByEventAndUser(event, user).ifPresent(regRepo::delete);
        return ResponseEntity.ok(Map.of("message", "Unregistered successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Event deleted"));
    }
}
