package com.example.customer.controller;

import com.example.customer.configuration.Properties;
import com.example.customer.ddd.model.redis.User;
import com.example.customer.ddd.model.redis.WebsocketUser;
import com.example.customer.ddd.service.WebSocketMessageHandler;
import com.example.customer.utility.MakeContent;
import com.example.customer.utility.OperationType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebsocketController {


    final WebSocketMessageHandler messageHandler;

    public WebsocketController(WebSocketMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/websocket", webSocketHandler());

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        //handlerMapping.setCorsConfigurations(Collections.singletonMap("*", new CorsConfiguration().applyPermitDefaultValues()));
        handlerMapping.setOrder(-1); // before annotated controllers
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    //https://docs.spring.io/spring-framework/reference/web/webflux-websocket.html
    @Bean
    WebSocketHandler webSocketHandler() {
        return new WebSocketHandler() {
            @Override
            public Mono<Void> handle(WebSocketSession session) {

                String username = session.getHandshakeInfo().getUri().getQuery();
                username = username.split("=")[1];

                session.getAttributes().put("username", username);

                WebsocketUser websocketUser = new WebsocketUser()
                        .setHost(Properties.Localhost)
                        .setPort(Properties.getServerPort())
                        .setId(session.getId());

                User user = new User()
                        .setUsername(username)
                        .addWebsocketUser(websocketUser);

                // OnOpen
                messageHandler.onOpen(user, session).subscribe();

                return session.receive()
                        .map(
                                message -> {// onMessage
                                    if (message.getType().toString().equals("TEXT")) {
                                        String text = message.getPayloadAsText(Charset.forName("UTF-8"));
                                        MakeContent content = MakeContent.get(OperationType.CHAT, text);
                                        messageHandler.broadCast(user, content).subscribe();
                                        return message;
                                    }
                                    return message;
                                }
                        )
                        .doOnError(throwException -> {
                            if (!session.isOpen()) {
                                messageHandler.onClose(user, websocketUser).subscribe();
                            }
                        })
                        .doOnTerminate(() -> {//After onClose
                            if (!session.isOpen()) {
                                messageHandler.onClose(user, websocketUser).subscribe();
                            }
                        })
                        .doOnCancel(() -> {
                            if (!session.isOpen()) {
                                messageHandler.onClose(user, websocketUser).subscribe();
                            }
                        })
                        .reduce((a, b) -> {
                            return a;
                        })
                        .flatMap(a -> {
                            return Mono.empty();
                        });

            }
        };


    }


}
