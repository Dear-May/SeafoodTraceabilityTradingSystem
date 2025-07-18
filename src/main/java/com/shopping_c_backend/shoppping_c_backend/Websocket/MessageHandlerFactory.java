package com.shopping_c_backend.shoppping_c_backend.Websocket;

import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.ChatMessageHandler;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.NotificationMessageHandler;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.PaymentMessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandlerFactory {

    private static final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();

    static {
        handlers.put("payment", new PaymentMessageHandler());
        handlers.put("notification", new NotificationMessageHandler());
        handlers.put("chat", new ChatMessageHandler());
    }

    public static MessageHandler getHandler(String module) {
        return handlers.getOrDefault(module, (id, message) -> {
            System.out.println("[default] 未知模块：" + module + "，消息：" + message);
        });
    }
}
