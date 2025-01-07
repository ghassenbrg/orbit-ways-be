package io.ghassen.orbitways.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 1) Register an endpoint for STOMP connections
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/orbitways-websocket")
                .setAllowedOriginPatterns("*")  // or specify domain(s)
                .withSockJS(); // fallback for old browsers
    }

    // 2) Configure message broker
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // prefix for messages from client to server (app prefix)
        registry.setApplicationDestinationPrefixes("/app");
        // prefix for messages from server to client (topic or queue)
        registry.enableSimpleBroker("/topic");
    }
}
