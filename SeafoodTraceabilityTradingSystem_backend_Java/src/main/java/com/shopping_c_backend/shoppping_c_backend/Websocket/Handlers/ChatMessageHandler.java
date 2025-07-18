package com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers;

import com.shopping_c_backend.shoppping_c_backend.Websocket.MessageHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageHandler.class);

    @Override
    public void handle(String id, String message) {
        LOGGER.info("[chat] 处理聊天消息：id={}，message={}", id, message);

        try {
            // 解析消息内容
            JSONObject jsonMessage = new JSONObject(message);
            int senderId = Integer.parseInt(jsonMessage.optString("senderId"));
            String senderType = jsonMessage.optString("senderType");
            int receiverId = Integer.parseInt(jsonMessage.optString("receiverId"));
            String receiverType = jsonMessage.optString("receiverType");
            String content = jsonMessage.optString("content");

        } catch (Exception e) {
            LOGGER.error("[chat] 处理聊天消息时发生错误：id={}，message={}, 错误信息={}", id, message, e.getMessage());
        }
    }

}
