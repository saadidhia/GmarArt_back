package com.germany.feroukart_back.util;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    /**
     * Generate JWT token for username
     */
    public String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Extract username from JWT token
     */
    public String extractUsername(String token) {
        try {
            Claims claims = getAllClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract expiration date from JWT token
     */
    public Date extractExpiration(String token) {
        try {
            Claims claims = getAllClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if token is expired
     */
    private Boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Validate token
     */
    public Boolean isTokenValid(String token) {
        try {
            getAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get all claims from token
     */
    private Claims getAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
