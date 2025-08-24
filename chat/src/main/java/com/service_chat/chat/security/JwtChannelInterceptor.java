package com.service_chat.chat.security;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import com.service_chat.chat.config.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null)
            return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = firstNativeHeader(accessor, "Authorization");
            if (StringUtils.hasText(authHeader)) {
                // fallback: support ?auth=Bearer%20<token> sur l'URL de handshake
                authHeader = (String) accessor.getSessionAttributes().get("Authorization");
            }

            if (StringUtils.hasText(authHeader) && authHeader.toLowerCase().startsWith("bearer ")) {
                String token = authHeader.substring(7).trim();
                try {
                    Claims claims = JwtUtil.validateToken(token);
                    String userId = claims.get("userId", String.class);
                    String role = claims.get("role", String.class);
                    if (!StringUtils.hasText(userId)) {
                        throw new IllegalArgumentException("JWT sans userId");
                    }
                    if (!StringUtils.hasText(role)) {
                        role = "USER"; // défaut sûr
                    }

                    JwtPrincipal principal = new JwtPrincipal(userId, role);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                    accessor.setUser(authentication); // pour convertAndSendToUser
                    SecurityContextHolder.getContext().setAuthentication(authentication); // pour @MessageMapping
                    log.debug("STOMP CONNECT authenticated userId={} role={}", userId, role);

                } catch (Exception e) {
                    log.warn("JWT invalid on STOMP CONNECT: {}", e.getMessage());
                    throw e; // bloque la connexion
                }
            } else {
                log.warn("Missing/invalid Authorization header on STOMP CONNECT");
                throw new IllegalArgumentException("Authorization Bearer token required");
            }
        } else if (SimpMessageType.MESSAGE.equals(accessor.getMessageType())) {
            // Renforce le SecurityContext sur chaque MESSAGE pour @MessageMapping
            if (accessor.getUser() != null) {
                SecurityContextHolder.getContext()
                        .setAuthentication((UsernamePasswordAuthenticationToken) accessor.getUser());
            }
        }
        return message;

    }

    private String firstNativeHeader(StompHeaderAccessor accessor, String name) {
        List<String> values = accessor.getNativeHeader(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

}
