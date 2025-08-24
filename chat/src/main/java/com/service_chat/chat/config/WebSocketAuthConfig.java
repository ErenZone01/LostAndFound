// package com.service_chat.chat.config;
// // ===== JWT -> STOMP bridge (ChannelInterceptor) =====
// import com.service_chat.chat.config.JwtUtil;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Primary;
// import org.springframework.messaging.Message;
// import org.springframework.messaging.MessageChannel;
// import org.springframework.messaging.simp.stomp.StompCommand;
// import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
// import org.springframework.messaging.support.ChannelInterceptor;
// import org.springframework.messaging.support.MessageHeaderAccessor;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.util.StringUtils;
// import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// import io.jsonwebtoken.Claims;

// import org.springframework.messaging.simp.config.ChannelRegistration;

// import java.util.List;

// @Configuration
// class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {

//     @Override
//     public void configureClientInboundChannel(ChannelRegistration registration) {
//         registration.interceptors(authChannelInterceptor());
//     }

//     @Bean
//     @Primary
//     public ChannelInterceptor authChannelInterceptor() {
//         return new ChannelInterceptor() {
//             @Override
//             public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                 StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//                 if (accessor == null)
//                     return message;

//                 if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//                     String authHeader = firstNativeHeader(accessor, "Authorization");
//                     if (!StringUtils.hasText(authHeader) || !authHeader.toLowerCase().startsWith("bearer ")) {
//                         throw new IllegalArgumentException("Authorization Bearer token required");
//                     }
//                     String token = authHeader.substring(7).trim();
//                     Claims claims = JwtUtil.validateToken(token);
//                     String userId = claims.get("userId", String.class); // le token contient userId
//                     String role = claims.get("role", String.class);
                   
                    
//                     if (!StringUtils.hasText(role))
//                         role = "USER";

//                     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                             userId,
//                             null,
//                             List.of(new SimpleGrantedAuthority("ROLE_" + role)));

//                     accessor.setUser(authentication); // pour convertAndSendToUser(userId,...)
//                     SecurityContextHolder.getContext().setAuthentication(authentication);
//                 }
//                 return message;
//             }

//             private String firstNativeHeader(StompHeaderAccessor accessor, String name) {
//                 List<String> values = accessor.getNativeHeader(name);
//                 return (values != null && !values.isEmpty()) ? values.get(0) : null;
//             }
//         };
//     }
// }