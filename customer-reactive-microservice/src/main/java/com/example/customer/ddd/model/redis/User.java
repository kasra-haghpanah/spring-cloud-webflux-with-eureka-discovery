package com.example.customer.ddd.model.redis;


import com.example.customer.configuration.Properties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashSet;
import java.util.Set;


public class User{


    private String username;
    Set<WebsocketUser> websocketUsers;

    public User() {
    }

    @Schema(description = "username", example = "kasra@gmail.com")
    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    @Schema(description = "Set of Websocket Users")
    public Set<WebsocketUser> getWebsocketUsers() {
        return websocketUsers;
    }

    public void setWebsocketUsers(Set<WebsocketUser> websocketUsers) {
        this.websocketUsers = websocketUsers;
    }

    @JsonIgnore
    public User addWebsocketUser(WebsocketUser... websocketUsers) {
        if (websocketUsers != null && websocketUsers.length > 0) {
            if (this.websocketUsers == null) {
                this.websocketUsers = new HashSet<>();
            }
            for (WebsocketUser websocketUser : websocketUsers) {
                this.websocketUsers.add(websocketUser);
            }
        }
        return this;
    }

    @JsonIgnore
    public User addWebsocketUser(String hashKey) {
        //System.out.println(hashKey);
        String[] keys = hashKey.split("-");
        WebsocketUser websocketUser = new WebsocketUser()
                .setHost(keys[0])
                .setPort(Integer.valueOf(keys[1]))
                .setId(keys[2]);
        this.addWebsocketUser(websocketUser);
        return this;
    }

    @JsonIgnore
    public User removeWebsocketUser(String wsSessionId) {
        this.websocketUsers.remove(
                new WebsocketUser()
                        .setHost(Properties.Localhost)
                        .setPort(Properties.getServerPort())
                        .setId(wsSessionId)
        );
        return this;
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

    @JsonIgnore
    public String toString(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
