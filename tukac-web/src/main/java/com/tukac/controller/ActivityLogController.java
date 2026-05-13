package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.ActivityLog;
import com.tukac.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activity-logs")
@PreAuthorize("hasRole('CHAIRPERSON')")
public class ActivityLogController {

    @Autowired private ActivityLogRepository activityLogRepository;

    /**
     * GET /api/activity-logs
     * GET /api/activity-logs?action=LOGIN
     * GET /api/activity-logs?user=John
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String user) {

        List<ActivityLog> logs;

        if (action != null && !action.isBlank()) {
            logs = activityLogRepository.findByActionOrderByTimestampDesc(action.trim().toUpperCase());
        } else if (user != null && !user.isBlank()) {
            logs = activityLogRepository.findByUserNameContainingIgnoreCaseOrderByTimestampDesc(user.trim());
        } else {
            logs = activityLogRepository.findAllByOrderByTimestampDesc();
        }

        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    /**
     * GET /api/activity-logs/stats
     * Returns count of events grouped by action type.
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        for (String action : List.of("LOGIN", "LOGOUT", "REGISTER", "PASSWORD_RESET",
                "REPORT_GENERATED", "TRANSACTION_ADDED", "EVENT_CREATED", "ROLE_CHANGED")) {
            long count = activityLogRepository.countByAction(action);
            if (count > 0) {
                stats.put(action, count);
            }
        }
        stats.put("TOTAL", activityLogRepository.count());
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    /**
     * DELETE /api/activity-logs/{id}
     * Remove a single log entry.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteLog(@PathVariable Long id) {
        if (!activityLogRepository.existsById(id)) {
            return ResponseEntity.status(404).body(ApiResponse.error("Log entry not found."));
        }
        activityLogRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Log entry deleted.", null));
    }

    /**
     * DELETE /api/activity-logs/clear
     * Wipe all log entries (destructive — chairperson only).
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearAllLogs() {
        long count = activityLogRepository.count();
        activityLogRepository.deleteAll();
        return ResponseEntity.ok(ApiResponse.ok("Cleared " + count + " log entries.", null));
    }
}
