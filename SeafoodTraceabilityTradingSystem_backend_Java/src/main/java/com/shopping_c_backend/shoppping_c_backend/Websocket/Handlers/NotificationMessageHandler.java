package com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers;

import com.shopping_c_backend.shoppping_c_backend.Websocket.MessageHandler;
import com.shopping_c_backend.shoppping_c_backend.Websocket.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationMessageHandler.class);

    @Override
    public void handle(String id, String message) {
        LOGGER.info("[notification] 处理通知消息：id={}，message={}", id, message);

        try {
            // 解析消息
            JSONObject jsonMessage = new JSONObject(message);
            String type = jsonMessage.optString("type");
            String content = jsonMessage.optString("message");
            boolean isUser;
            try {
                isUser = jsonMessage.optBoolean("isUser");
            } catch (Exception e) {
                isUser = false;
            }

            // 根据通知类型处理
            switch (type) {
                case "order":
                    handleOrderNotification(id, content);
                    break;
                case "return":
                    handleReturnNotification(id, content);
                    break;
                case "new_employee":
                    handleNewEmployeeNotification(id, content);
                    break;
                case "chat":
                    handleChatNotification(id, content, isUser);
                    break;
                case "live":
                    handleLiveNotification(id, content);
                default:
                    LOGGER.warn("[notification] 未知通知类型：type={}, id={}, message={}", type, id, message);
            }
        } catch (JSONException e) {
            LOGGER.error("[notification] JSON解析错误：id={}, message={}, 错误信息={}", id, message, e.getMessage());
        }
    }

    // 处理订单通知
    private void handleOrderNotification(String id, String content) {
        LOGGER.info("[notification] 订单通知：id={}, content={}", id, content);
        // 推送订单通知到商家端
        String notificationMessage = createNotificationMessage("order", content);
        SessionManager.sendMessage("merchant", id, notificationMessage);
    }

    // 处理退换货通知
    private void handleReturnNotification(String id, String content) {
        LOGGER.info("[notification] 退换货通知：id={}, content={}", id, content);
        // 推送退换货通知到商家端
        String notificationMessage = createNotificationMessage("return", content);
        SessionManager.sendMessage("merchant", id, notificationMessage);
    }

    // 处理新员工通知
    private void handleNewEmployeeNotification(String id, String content) {
        LOGGER.info("[notification] 新员工通知：id={}, content={}", id, content);
        // 推送新员工通知到商家端
        String notificationMessage = createNotificationMessage("new_employee", content);
        SessionManager.sendMessage("merchant", id, notificationMessage);
    }

    // 处理聊天通知
    private void handleChatNotification(String id, String content, boolean toUser) {
        LOGGER.info("[notification] 聊天通知：id={}, content={}, toUser={}", id, content, toUser);
        // 推送聊天通知到商家端
        String notificationMessage = createNotificationMessage("chat", content);
        if (toUser)
            SessionManager.sendMessage("merchant", id, notificationMessage);
        else
            SessionManager.sendMessage("user", id, notificationMessage);
    }

    // 处理直播通知
    private void handleLiveNotification(String id, String content) {
        LOGGER.info("[notification] 直播通知：id={}, content={}", id, content);
        // 推送直播通知到live
        String notificationMessage = createNotificationMessage("live", content);
        SessionManager.sendMessage("live", id, notificationMessage);
    }

    // 创建通知消息
    private String createNotificationMessage(String type, String content) {
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", type);
            jsonMessage.put("message", content);
            return jsonMessage.toString();
        } catch (JSONException e) {
            LOGGER.error("[notification] 创建通知消息JSON错误：type={}, content={}, 错误信息={}", type, content, e.getMessage());
            return null;
        }
    }
}
