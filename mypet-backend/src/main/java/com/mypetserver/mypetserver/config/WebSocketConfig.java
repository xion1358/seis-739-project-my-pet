package com.mypetserver.mypetserver.config;

import com.mypetserver.mypetserver.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);
    private final UserDetailsService ownerDetailsService;
    private final TokenService tokenService;

    @Autowired
    public WebSocketConfig(UserDetailsService userDetailsService, TokenService tokenService) {
        this.ownerDetailsService = userDetailsService;
        this.tokenService = tokenService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-pet").setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = getTokenFromHeader(accessor);
                    if (token != null && tokenService.validateJWTToken(token)) {
                        Authentication authentication = authenticateUser(token);
                        if (authentication != null) {
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.setUser(authentication);
                        }
                    }
                }
                return message;
            }
        });
    }

    private String getTokenFromHeader(StompHeaderAccessor accessor) {
        List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String token = authorizationHeaders.get(0);
            if (token.startsWith("Bearer ")) {
                return token.substring(7);  // Remove "Bearer " prefix
            }
        }
        return null;
    }

    private Authentication authenticateUser(String token) {
        try {
            String username = tokenService.parseUsernameFromJWT(token);
            if (username == null) {
                logger.warn("Username not found in JWT");
                return null;
            }

            UserDetails userDetails = ownerDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                logger.warn("User {} not found", username);
                return null;
            }

            return new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

        } catch (Exception e) {
            logger.error("Error authenticating user with token: {}. Error: {}", token, e.getMessage());
        }
        return null;
    }

}
