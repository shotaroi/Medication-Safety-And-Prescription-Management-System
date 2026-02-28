package com.shotaroi.medsafety.api;

import com.shotaroi.medsafety.infrastructure.security.JwtService;
import com.shotaroi.medsafety.infrastructure.security.SecurityRoles;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Development token endpoint. In production, use a proper auth provider (OAuth2, etc.).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> getToken(
            @RequestParam(defaultValue = "doctor") String user,
            @RequestParam(defaultValue = "DOCTOR") String role) {
        String token = jwtService.generateToken(user, role);
        return ResponseEntity.ok(Map.of("token", token, "role", role));
    }
}
