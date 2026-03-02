package com.shotaroi.medsafety.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Minimal controller for API readiness check.
 * Full health details available at /actuator/health.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/ready")
    public ResponseEntity<Map<String, String>> ready() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "medication-safety-api"));
    }
}
