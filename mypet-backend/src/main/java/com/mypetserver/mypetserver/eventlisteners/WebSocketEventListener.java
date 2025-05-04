package com.mypetserver.mypetserver.eventlisteners;

import com.mypetserver.mypetserver.services.PetManagerService;
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
    private final PetManagerService petManagerService;

    public WebSocketEventListener(PetManagerService petManagerService) {
        this.petManagerService = petManagerService;
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        petManagerService.removeSubscriberBySessionId(sessionId);
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        //logger.info("WebSocket session subscribe event. Session ID: {}", sessionId);
        if (destination != null) {
            String topicLocation = null;

            if (destination.startsWith("/topic/pet/")) {
                topicLocation = "/topic/pet/";
            } else if (destination.startsWith("/topic/shared/pet/")) {
                topicLocation = "/topic/shared/pet/";
            }

            if (topicLocation != null) {
                try {
                    int petId = Integer.parseInt(destination.replace(topicLocation, ""));
                    petManagerService.addSubscriber(petId, sessionId);
                } catch (NumberFormatException ignored) {
                    logger.error("Could not parse pet id from destination: {}", destination);
                }
            }
        }

    }
}
