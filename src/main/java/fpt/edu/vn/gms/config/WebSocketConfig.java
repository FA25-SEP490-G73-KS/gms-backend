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

        // Topic để server gửi message đến client
        registry.enableSimpleBroker("/topic", "/queue");
        // Prefix để client gửi message đến server
        registry.setApplicationDestinationPrefixes("/app");
    }
}
