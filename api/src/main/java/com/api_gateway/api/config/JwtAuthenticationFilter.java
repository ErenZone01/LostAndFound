package com.api_gateway.api.config;

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    public JwtAuthenticationFilter(JwtAuthenticationManager authenticationManager) {
        super(authenticationManager);

        setServerAuthenticationConverter(new ServerAuthenticationConverter() {
            @Override
            public Mono<org.springframework.security.core.Authentication> convert(ServerWebExchange exchange) {
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    return Mono.just(new JwtAuthenticationToken(token));
                }
                return Mono.empty();
            }
        });
    }
}
