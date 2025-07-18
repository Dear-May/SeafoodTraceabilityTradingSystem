package com.shopping_c_backend.shoppping_c_backend.Websocket;

public interface MessageHandler {
    void handle(String id, String message);
}
