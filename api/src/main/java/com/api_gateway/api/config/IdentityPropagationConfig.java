package com.api_gateway.api.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import reactor.core.publisher.Mono;

@Configuration
public class IdentityPropagationConfig {

    @Bean
    public GlobalFilter userHeadersPropagationFilter() {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // Retirer "Bearer "

                try {
                    // Décoder et vérifier le token
                    Claims claims = JwtUtil.validateToken(token);

                    String userId = claims.get("userId", String.class);
                    String role = claims.get("role", String.class);

                    if (userId == null || role == null) {
                        return Mono.error(new BadCredentialsException("Invalid JWT Token"));
                    }

                    

                    // Injecter les infos utilisateur dans les headers
                    ServerHttpRequest mutatedRequest = exchange.getRequest()
                            .mutate()
                            .headers(h -> {
                                h.remove("X-User-Id");
                                h.remove("X-User-Role");
                                h.add("X-User-Id", userId);
                                h.add("X-User-Role", role);
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());

                } catch (ExpiredJwtException e) {
                    System.out.println("JWT expiré : " + e.getMessage());
                    return Mono.error(new BadCredentialsException("JWT Expired"));
                } catch (JwtException e) {
                    System.out.println("JWT invalide : " + e.getMessage());
                    return Mono.error(new BadCredentialsException("Invalid JWT"));
                }
            }

            // Si pas de header Authorization → on continue normalement (cas routes publiques)
            return chain.filter(exchange);
        };
    }
}
