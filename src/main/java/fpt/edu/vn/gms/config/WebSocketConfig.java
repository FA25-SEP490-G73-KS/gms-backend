package fpt.edu.vn.gms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Endpoint để client kết nối
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // hoặc domain cụ thể
                .withSockJS(); // fallback nếu browser không hỗ trợ WebSocket
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // messages to client are sent to /user/... or /topic/...
        registry.enableSimpleBroker("/topic", "/queue");
        // prefix for messages from client to server
        registry.setApplicationDestinationPrefixes("/app");
        // identify destination for a specific user
        registry.setUserDestinationPrefix("/user");
    }
}
