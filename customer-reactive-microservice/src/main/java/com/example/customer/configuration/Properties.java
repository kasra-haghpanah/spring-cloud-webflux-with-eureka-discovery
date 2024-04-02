package com.example.customer.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

@Configuration("properties")
public class Properties {

    public static String Localhost = "localhost";
    private static final Map<String, Object> config = new HashMap<String, Object>();

    public Properties(Environment environment) {

        config.put("server.port", environment.getProperty("server.port"));
        config.put("spring.application.name", environment.getProperty("spring.application.name"));
        config.put("springdoc.api-docs.version", environment.getProperty("springdoc.api-docs.version"));
        config.put("springdoc.packagesToScan", environment.getProperty("springdoc.packagesToScan"));

        config.put("redis.host", environment.getProperty("redis.host"));
        config.put("redis.port", environment.getProperty("redis.port"));

    }

    private static <T> T get(String key, Class<T> T) {
        return (T) config.get(key);
    }

    public static Integer getServerPort() {
        return Integer.valueOf(get("server.port", String.class));
    }

    public static String getApplicationName() {
        return get("spring.application.name", String.class);
    }

    public static String getSpringdocApidocsVersion() {
        return get("springdoc.api-docs.version", String.class);
    }

    public static String getSpringdocPackagesToScan() {
        return get("springdoc.packagesToScan", String.class);
    }

    public static String getRedisHost() {
        return get("redis.host", String.class);
    }

    public static Integer getRedisPort() {
        return Integer.valueOf(get("redis.port", String.class));
    }

}
