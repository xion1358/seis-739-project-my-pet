package com.mypetserver.mypetserver.eventlisteners;

import com.mypetserver.mypetserver.managers.PetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final PetManager petManager;

    public WebSocketEventListener(PetManager petManager) {
        this.petManager = petManager;
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        petManager.removeSubscriberBySessionId(sessionId);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        //logger.info("WebSocket session subscribe event. Session ID: {}", sessionId);
        if (destination != null && destination.startsWith("/topic/pet/")) {
            try {
                int petId = Integer.parseInt(destination.replace("/topic/pet/", ""));
                petManager.addSubscriber(petId, sessionId);
            } catch (NumberFormatException ignored) {}
        }
    }
}
