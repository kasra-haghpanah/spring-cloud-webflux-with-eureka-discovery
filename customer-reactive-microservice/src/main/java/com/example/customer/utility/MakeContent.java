package com.example.customer.utility;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MakeContent<T> {

    @JsonIgnore
    ObjectMapper objectMapper;
    OperationType operationType;
    String sender;
    String receiver;
    String webSocketId;
    T content;

    public MakeContent() {
    }

    private MakeContent(OperationType operationType, T content) {
        this.operationType = operationType;
        this.content = content;
    }

    public static <T> MakeContent get(OperationType operationType, T content) {
        return new MakeContent<T>(operationType, content);
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getSender() {
        return sender;
    }

    public MakeContent setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public MakeContent setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public String getWebSocketId() {
        return webSocketId;
    }

    public MakeContent setWebSocketId(String webSocketId) {
        this.webSocketId = webSocketId;
        return this;
    }

    public T getContent() {
        return content;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public MakeContent setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public String toJson() {

        if (this.objectMapper == null) {
            this.objectMapper = new ObjectMapper();
        }

        if (this.content == null) {
            throw new RuntimeException("Enter the message value");
        }

        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
