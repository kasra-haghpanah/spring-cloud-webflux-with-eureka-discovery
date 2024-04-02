package com.example.customer.utility;

public enum OperationType {

    CHAT("chat"), OPEN_SESSION("open-session"), CLOSE_SESSION("close-session");
    String key;

    OperationType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
