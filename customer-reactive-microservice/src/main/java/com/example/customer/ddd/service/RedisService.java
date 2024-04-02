package com.example.customer.ddd.service;

import com.example.customer.ddd.model.redis.User;
import com.example.customer.utility.MakeContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactivestreams.Publisher;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RedisService {


    final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    final ObjectMapper objectMapper;

    public RedisService(ReactiveStringRedisTemplate reactiveStringRedisTemplate, ObjectMapper objectMapper) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    public Mono<Long> convertAndSend(String topicName, MakeContent content) {
        return this.reactiveStringRedisTemplate.convertAndSend(topicName, content.setObjectMapper(objectMapper).toJson());
    }

    public Flux<String> removeWebSocketSession(String username, String host, int port, String wsSessionId) {
        String hashKey = host + "-" + port + "-" + wsSessionId;
        return this.reactiveStringRedisTemplate.opsForHash().remove(username, hashKey)
                .flatMapMany(aLong -> {
                    return this.reactiveStringRedisTemplate.opsForHash().keys(username);
                })
                .count()
                .flatMapMany(hashKeyNumbers -> {

                    if (hashKeyNumbers == 0) {
                        return this.reactiveStringRedisTemplate.opsForHash().delete(username)
                                .flatMapMany(aBoolean -> {
                                    return Flux.empty();
                                });
                    }
                    return this.reactiveStringRedisTemplate.opsForHash().keys(username)
                            .map(hashKeyNode -> {
                                return (String) hashKeyNode;
                            });

                });

    }

    public Mono<User> removeWebSocket(String username, String host, int port, String wsSessionId) {
        return this.removeWebSocketSession(username, host, port, wsSessionId)
                .reduce(new User().setUsername(username), (user, hashKey) -> {
                    return user.addWebsocketUser(hashKey);
                });
    }

    public Flux<String> addWebSocketSession(String username, String host, int port, String wsSessionId) {

        String hashKey = host + "-" + port + "-" + wsSessionId;
        return this.reactiveStringRedisTemplate.opsForHash().putIfAbsent(username, hashKey, "")
                .flatMapMany(aBoolean -> {
                    return this.reactiveStringRedisTemplate.opsForHash().keys(username)
                            .map(hashKeyNode -> {
                                return (String) hashKeyNode;
                            });
                });

    }

    public Mono<User> addWebSocket(String username, String host, int port, String wsSessionId) {

        return this.addWebSocketSession(username, host, port, wsSessionId)
                .reduce(new User().setUsername(username), (user, hashKey) -> {
                    return user.addWebsocketUser(hashKey);
                });

    }

    public Flux<String> getAllKeys() {
        return this.reactiveStringRedisTemplate.keys("*")
                .map(hashKey -> {
                    return (String) hashKey;
                });
    }

    public Flux<String> getAllHashKeys() {
        return this.getAllKeys()
                .flatMap(key -> {
                    return this.reactiveStringRedisTemplate.opsForHash().keys(key)
                            .map(hashKey -> {
                                return (String) hashKey;
                            });
                });
    }

    public Flux<String> getAllHashKeysByKeys(Publisher<String> keys) {

        if (keys == null) {
            return Flux.empty();
        }

        return Flux.from(keys)
                .flatMap(key -> {
                    return this.reactiveStringRedisTemplate.opsForHash().keys(key)
                            .map(hashKey -> {
                                return (String) hashKey;
                            });
                });
    }

    public Flux<User> findAllUsers() {
        return findAllUsersByKeys(this.getAllKeys());
    }

    public Flux<User> findAllUsersByKeys(Publisher<String> keys) {
        return Flux.from(keys)
                .flatMap(key -> {
                    return this.reactiveStringRedisTemplate.opsForHash().keys(key)
                            .reduce(new User().setUsername(key), (user, hashKey) -> {
                                return user.addWebsocketUser((String) hashKey);
                            });
                });
    }

    public Flux<User> findAllUsersByKeys(String... keys) {
        if (keys == null || keys.length == 0) {
            return Flux.empty();
        }
        return findAllUsersByKeys(Flux.fromArray(keys));
    }

    public Flux<User> findAllUsersByKeys(Iterable<String> keys) {
        if (keys == null || !keys.iterator().hasNext()) {
            return Flux.empty();
        }
        return findAllUsersByKeys(Flux.fromIterable(keys));
    }

    public Flux<String> getAllHashKeysByKeys(String... keys) {
        if (keys == null || keys.length == 0) {
            return Flux.empty();
        }
        return getAllHashKeysByKeys(Flux.fromArray(keys));
    }

    public Flux<String> getAllHashKeysByKeys(Iterable<String> keys) {
        if (keys == null || !keys.iterator().hasNext()) {
            return Flux.empty();
        }
        return getAllHashKeysByKeys(Flux.fromIterable(keys));
    }


}
