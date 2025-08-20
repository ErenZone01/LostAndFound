package com.api_gateway.api.config;

import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {



    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();

        try {
            // Décoder et vérifier le token
            Claims claims = JwtUtil.validateToken(token);

            String userId = claims.getSubject().toString();
            String role = claims.get("role", String.class);

            System.out.println("userId: " + userId);
            System.out.println("role: " + role);

            if (userId == null || role == null) {
                return Mono.error(new BadCredentialsException("Invalid JWT Token"));
            }

            // Crée ton CustomUserDetails avec les données du token
            CustomUserDetails userDetails = new CustomUserDetails(
                    userId,
                    null, // pas besoin de password ici
                    role,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            return Mono.just(authToken);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return Mono.error(new BadCredentialsException("JWT expired"));
        } catch (io.jsonwebtoken.JwtException e) {
            return Mono.error(new BadCredentialsException("Invalid JWT Token"));
        }
    }

}
