package com.shopping_c_backend.websocket.core;

public interface MessageHandler {
    void handle(String id, String message);
}
