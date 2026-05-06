package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.Event;
import com.tukac.model.EventRegistration;
import com.tukac.repository.EventRegistrationRepository;
import com.tukac.repository.EventRepository;
import com.tukac.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired private EventRepository eventRepository;
    @Autowired private EventRegistrationRepository registrationRepository;
    @Autowired private JwtService jwtService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEvents(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try { userId = jwtService.extractUserId(authHeader.substring(7)); } catch (Exception ignored) {}
        }

        final Long finalUserId = userId;
        List<Event> events = eventRepository.findAllByOrderByEventDateAsc();

        List<Map<String, Object>> result = events.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("description", e.getDescription());
            map.put("eventDate", e.getEventDate());
            map.put("eventTime", e.getEventTime());
            map.put("location", e.getLocation());
            map.put("capacity", e.getCapacity());
            map.put("rsvpCount", registrationRepository.countByEventId(e.getId()));
            map.put("isRsvped", finalUserId != null && registrationRepository.existsByUserIdAndEventId(finalUserId, e.getId()));
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','SECRETARY')")
    public ResponseEntity<ApiResponse<Event>> createEvent(@RequestBody Event event, Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        event.setCreatedBy(userId);
        Event saved = eventRepository.save(event);
        return ResponseEntity.ok(ApiResponse.ok("Event created", saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','SECRETARY')")
    public ResponseEntity<ApiResponse<Event>> updateEvent(@PathVariable Long id, @RequestBody Event updated) {
        Optional<Event> opt = eventRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Event event = opt.get();
        event.setTitle(updated.getTitle());
        event.setDescription(updated.getDescription());
        event.setEventDate(updated.getEventDate());
        event.setEventTime(updated.getEventTime());
        event.setLocation(updated.getLocation());
        event.setCapacity(updated.getCapacity());

        return ResponseEntity.ok(ApiResponse.ok("Event updated", eventRepository.save(event)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','SECRETARY')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) return ResponseEntity.notFound().build();
        eventRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Event deleted", null));
    }

    @PostMapping("/{id}/rsvp")
    public ResponseEntity<ApiResponse<String>> rsvp(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getCredentials();

        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (registrationRepository.existsByUserIdAndEventId(userId, id)) {
            // Cancel RSVP
            registrationRepository.findByUserIdAndEventId(userId, id)
                    .ifPresent(r -> registrationRepository.delete(r));
            return ResponseEntity.ok(ApiResponse.ok("RSVP cancelled", null));
        }

        registrationRepository.save(new EventRegistration(userId, id));
        return ResponseEntity.ok(ApiResponse.ok("RSVP confirmed!", null));
    }
}
