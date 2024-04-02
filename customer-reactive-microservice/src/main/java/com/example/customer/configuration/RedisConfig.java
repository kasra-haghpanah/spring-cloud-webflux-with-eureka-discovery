package com.example.customer.configuration;

import com.example.customer.ddd.service.RedisService;
import com.example.customer.ddd.service.WebSocketMessageHandler;
import com.example.customer.utility.MakeContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Configuration
@DependsOn({"properties"})
//@EnableRedisRepositories(basePackages = {"com.example.customer.ddd.repository"})
//@EnableTransactionManagement
public class RedisConfig {

    final ObjectMapper objectMapper;

    public RedisConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    @Qualifier("reactiveRedisConnectionFactory")
    ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                //.useSsl().and()
                //.readFrom(ReadFrom.REPLICA_PREFERRED)
                .commandTimeout(Duration.ofSeconds(100))
                .shutdownTimeout(Duration.ZERO)
                .build();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();

        redisStandaloneConfiguration.setHostName(Properties.getRedisHost());
        redisStandaloneConfiguration.setPort(Properties.getRedisPort());
        //redisStandaloneConfiguration.setPassword(password);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

    @Bean
    @Qualifier("reactiveStringRedisTemplate")
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(@Qualifier("reactiveRedisConnectionFactory") ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }

    public static String getTopicName() {
        return Properties.Localhost + "-" + Properties.getServerPort();
    }

    @Bean
    public ChannelTopic messageTopic() {
        return new ChannelTopic(getTopicName());
    }

    @Bean
    public ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer(
            @Qualifier("reactiveRedisConnectionFactory") ReactiveRedisConnectionFactory connectionFactory,
            RedisService redisService
    ) {
        ReactiveRedisMessageListenerContainer container = new ReactiveRedisMessageListenerContainer(connectionFactory);
        container.receive(ChannelTopic.of(getTopicName())).subscribe(message -> {
            // Handle incoming message asynchronously

            try {
                MakeContent content = objectMapper.readValue(message.getMessage(), MakeContent.class);
                content.setObjectMapper(objectMapper);

                WebSocketSession webSocketSession = WebSocketMessageHandler.wsSessions.get(content.getWebSocketId());
                if (webSocketSession == null) {
                    redisService.removeWebSocketSession(content.getReceiver(), Properties.Localhost, Properties.getServerPort(), content.getWebSocketId()).subscribe();
                    return;
                }
                webSocketSession.send(Mono.just(message.getMessage()).map(webSocketSession::textMessage)).subscribe();
                System.out.println("Received message: " + content.toJson());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        });
        return container;
    }


}
