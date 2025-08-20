package com.service_auth.auth.config;

import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    private static final String SECRET_KEY = "Lost-And-Found-secret-key-for-jwt-123456"; // À sécuriser en prod
    private static final long EXPIRATION_TIME = 86400000; // 1 jour en ms
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(String userId, String role) {
        return Jwts.builder()
            .setSubject(userId)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(KEY, SignatureAlgorithm.HS256)
            .compact();
    }
}
