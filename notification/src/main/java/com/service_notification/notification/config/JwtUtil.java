package com.service_notification.notification.config;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


public class JwtUtil {
    private static final String SECRET_KEY = "ThisIsASuperSecureAndVeryLongJwtSecretKey1234567890!@#"; // À sécuriser en prod
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extraire les claims
}
