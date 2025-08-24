// File: src/main/java/com/service_chat/chat/security/JwtPrincipal.java
package com.service_chat.chat.security;

import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Principal porté sur les frames STOMP. getName() == userId (utilisé par
 * convertAndSendToUser).
 */
@AllArgsConstructor
@Getter
public class JwtPrincipal implements Principal {
    private final String userId;
    private final String role; // e.g. USER, ADMIN

    @Override
    public String getName() {
        return userId;
    }
}