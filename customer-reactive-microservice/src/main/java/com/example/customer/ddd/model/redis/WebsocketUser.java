package com.example.customer.ddd.model.redis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebsocketUser {

    private String host;
    private int port;
    private String id;

    public WebsocketUser() {
    }

    @Schema(description = "host")
    public String getHost() {
        return host;
    }

    public WebsocketUser setHost(String host) {
        this.host = host;
        return this;
    }
    @Schema(description = "port")
    public int getPort() {
        return port;
    }

    public WebsocketUser setPort(int port) {
        this.port = port;
        return this;
    }
    @Schema(description = "id")
    public String getId() {
        return id;
    }

    public WebsocketUser setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    @JsonIgnore
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebsocketUser that = (WebsocketUser) o;
        return port == that.port && Objects.equals(host, that.host) && Objects.equals(id, that.id);
    }

    @Override
    @JsonIgnore
    public int hashCode() {
        return Objects.hash(host, port, id);
    }

    @Override
    @JsonIgnore
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
