package com.example.customer.configuration;

import com.example.customer.ddd.service.RedisService;
import com.example.customer.ddd.service.WebSocketMessageHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.web.reactive.socket.WebSocketSession;

@Configuration
public class EventConfig {

    final RedisService redisService;

    public EventConfig(RedisService redisService) {
        this.redisService = redisService;
    }

    @Bean
    ApplicationListener<ContextClosedEvent> closedEvent() {
        // when the application gets shut down should be removed all sessions related to this application on the Redis database
        return (contextClosedEvent) -> {

            for (String id : WebSocketMessageHandler.wsSessions.keySet()) {
                WebSocketSession webSocketSession = WebSocketMessageHandler.wsSessions.get(id);
                String username = (String) webSocketSession.getAttributes().get("username");
                redisService.removeWebSocketSession(username, Properties.Localhost, Properties.getServerPort(), id).subscribe();
            }
            System.out.println("All WebSockets data related to this application have been removed from the Redis database.");
        };

    }

}
