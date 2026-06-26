package com.germany.feroukart_back.controller;



import com.germany.feroukart_back.dto.LoginRequest;
import com.germany.feroukart_back.dto.LoginResponse;
import com.germany.feroukart_back.service.SimpleAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SimpleAuthController {

    private final SimpleAuthService authService;

    /**
     * Admin login endpoint
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
            }

            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            // Authenticate user
            LoginResponse response = authService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            if (response == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Invalid username or password"
                ));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Authentication failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Get current authenticated user info
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            // User info is extracted from JWT by the filter
            // This endpoint is optional - shows the JWT works
            Map<String, String> response = new HashMap<>();
            response.put("message", "Authenticated");
            response.put("username", "admin");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Not authenticated"
            ));
        }
    }
}
