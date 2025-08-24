package com.service_notification.notification.config;

import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;

@Component
public class AuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    Claims claims = JwtUtil.validateToken(token);
                    String userId = claims.getSubject(); // ⚡ l’UUID de ton user

                    Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of());

                    accessor.setUser(authentication); // 👈 important : ça set le Principal
                    System.out.println("🚀 STOMP CONNECT authentifié: " + authentication.getName());
                } catch (Exception e) {
                    System.out.println("❌ JWT invalide: " + e.getMessage());
                    return null; // bloque la connexion
                }
            } else {
                System.out.println("❌ Aucun Authorization header dans le CONNECT");
                return null;
            }
        }

        return message;
    }
}
