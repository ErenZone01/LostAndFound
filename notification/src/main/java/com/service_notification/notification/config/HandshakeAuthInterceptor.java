// File: src/main/java/com/service_chat/chat/config/HandshakeAuthInterceptor.java
package com.service_notification.notification.config;

// ===== Handshake query-param fallback interceptor (fix signature) =====
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Permet de passer ?auth=Bearer%20<token> sur l'URL de handshake SockJS si
 * besoin.
 * (La méthode afterHandshake corrige la signature demandée par Spring)
 */
public class HandshakeAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        var uri = request.getURI();
        var query = uri.getQuery();
        if (query != null) {
            for (String part : query.split("&")) {
                if (part.startsWith("auth=")) {
                    attributes.put("Authorization", part.substring(5));
                    break;
                }
            }
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("🚀 Connexion WS: principal.getName() = " + auth.getName());
        }
        return true;
        // throw new UnsupportedOperationException("Unimplemented method
        // 'beforeHandshake'");
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            @Nullable Exception exception) {
    }

}