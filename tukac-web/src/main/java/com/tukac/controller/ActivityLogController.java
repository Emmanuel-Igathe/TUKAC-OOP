package com.tukac.controller;

import com.tukac.dto.ApiResponse;
import com.tukac.model.ActivityLog;
import com.tukac.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@PreAuthorize("hasRole('CHAIRPERSON')")
public class ActivityLogController {

    @Autowired private ActivityLogRepository activityLogRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getLogs() {
        return ResponseEntity.ok(ApiResponse.ok(activityLogRepository.findAllByOrderByTimestampDesc()));
    }
}
