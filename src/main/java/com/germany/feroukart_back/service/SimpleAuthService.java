package com.germany.feroukart_back.service;


import com.germany.feroukart_back.dto.LoginResponse;
import com.germany.feroukart_back.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimpleAuthService {

    private final JwtUtil jwtUtil;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    /**
     * Authenticate user with username and password
     * No database needed - credentials from application.properties
     */
    public LoginResponse authenticate(String username, String password) {
        // Validate credentials against config values
        if (!username.equals(adminUsername) || !password.equals(adminPassword)) {
            return null; // Invalid credentials
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(username);

        // Create response
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(new LoginResponse.UserInfo(username));

        return response;
    }

    /**
     * Verify JWT token validity
     */
    public boolean verifyToken(String token) {
        try {
            String username = jwtUtil.extractUsername(token);
            return username != null && jwtUtil.isTokenValid(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract username from JWT token
     */
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }
}
