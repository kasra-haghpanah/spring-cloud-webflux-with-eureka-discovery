package com.example.customer.ddd.service;

import com.example.customer.configuration.Properties;
import com.example.customer.ddd.model.redis.User;
import com.example.customer.ddd.model.redis.WebsocketUser;
import com.example.customer.utility.MakeContent;
import com.example.customer.utility.OperationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketMessageHandler {

    public static final Map<String, WebSocketSession> wsSessions = new ConcurrentHashMap<>();
    final RedisService redisService;
    final ObjectMapper objectMapper;

    public WebSocketMessageHandler(RedisService redisService, ObjectMapper objectMapper) {
        this.redisService = redisService;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> onClose(User user, WebsocketUser websocketUser) {
        return this.onClose(user, websocketUser, this.redisService.getAllKeys());
    }

    public Mono<Void> onClose(User user, WebsocketUser websocketUser, Publisher<String> usernames) {

        if (wsSessions.get(websocketUser.getId()) != null) {
            wsSessions.remove(websocketUser.getId());
        }

        return redisService.removeWebSocket(user.getUsername(), websocketUser.getHost(), websocketUser.getPort(), websocketUser.getId())
                .defaultIfEmpty(new User().setUsername(user.getUsername()))
                .flatMap(user1 -> {// the current user announces to everyone that they have left the chat.
                    MakeContent content = MakeContent.get(OperationType.CLOSE_SESSION, user1);
                    return broadCast(user, content, usernames);
                });
    }

    public Mono<Void> onOpen(User user, WebSocketSession session) {
        return this.onOpen(user, session, this.redisService.getAllKeys());
    }

    public Mono<Void> onOpen(User user, WebSocketSession session, Publisher<String> usernames) {
        wsSessions.putIfAbsent(session.getId(), session);
        return redisService.addWebSocket(user.getUsername(), Properties.Localhost, Properties.getServerPort(), session.getId())
                .flatMap(userFromDb -> {
                    OperationType type = OperationType.OPEN_SESSION;
                    //userFromDb.toString(objectMapper);

                    session.send(this.redisService.findAllUsersByKeys(usernames) // The sessions of all online users are sent to the current user who has got online lately.
                            .filter(user1 -> {
                                return !user1.getUsername().equals(userFromDb.getUsername());
                            })
                            .map(user1 -> {
                                String json = MakeContent.get(type, user1)
                                        .setSender(userFromDb.getUsername())
                                        .setReceiver(userFromDb.getUsername())
                                        .toJson();
                                return session.textMessage(json);
                            })).subscribe();

                    MakeContent message = MakeContent.get(type, userFromDb).setObjectMapper(objectMapper);
                    // the session of the current user is sent to all online users even itself
                    return broadCast(user, message, usernames);// the user announces they are online to everyone except themselves.
                });
    }

    public Mono<Void> broadCast(User user, MakeContent content) {
        return broadCast(user, content, this.redisService.getAllKeys());
    }

    public Mono<Void> broadCast(User user, MakeContent content, Publisher<String> usernames) {

        return redisService.findAllUsersByKeys(usernames)
                .map(userFromDb -> {

                    content.setObjectMapper(objectMapper)
                            .setSender(user.getUsername())
                            .setReceiver(userFromDb.getUsername());

                    for (WebsocketUser socketSession : userFromDb.getWebsocketUsers()) {
                        if (socketSession.getHost().equals(Properties.Localhost) && socketSession.getPort() == Properties.getServerPort()) {
                            WebSocketSession session = wsSessions.get(socketSession.getId());
                            if (session == null) {
                                redisService.removeWebSocketSession(user.getUsername(), Properties.Localhost, Properties.getServerPort(), socketSession.getId()).subscribe();
                            } else {

                                if (session != null && session.isOpen()) {
                                    session.send(Mono.just(content.toJson()).map(session::textMessage)).subscribe();
                                }
                            }
                        } else {
                            String topicName = Properties.Localhost + "-" + socketSession.getPort();
                            content.setWebSocketId(socketSession.getId());
                            redisService.convertAndSend(topicName, content).subscribe();
                        }
                    }
                    return userFromDb;
                })
                .reduce(0, (number, user1) -> {
                    return number + 1;
                })
                .flatMap(o -> {
                    return Mono.empty();
                });

    }

}
