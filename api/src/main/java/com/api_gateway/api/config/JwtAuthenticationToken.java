package com.api_gateway.api.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public JwtAuthenticationToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES); // Pas de roles pour l'instant
        this.token = token;
        setAuthenticated(false); // Non authentifié au départ
    }

    public String getToken() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token; // JWT comme "credentials"
    }

    @Override
    public Object getPrincipal() {
        return null; // Pas encore d'utilisateur
    }
}
