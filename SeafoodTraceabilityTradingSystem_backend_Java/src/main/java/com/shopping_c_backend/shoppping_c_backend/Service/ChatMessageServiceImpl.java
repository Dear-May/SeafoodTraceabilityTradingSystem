package com.shopping_c_backend.shoppping_c_backend.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_c_backend.shoppping_c_backend.Entity.ChatMessageEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ChatSessionEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ChatMessageMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ShopMapper;
import com.shopping_c_backend.shoppping_c_backend.Util.DateUtil;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.ChatMessageHandler;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.NotificationMessageHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ChatMessageServiceImpl {
    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Resource
    private ShopMapper shopMapper;
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    public Map<String, Object> getUnreadMessageCount(int userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("count", chatMessageMapper.getUnreadCountByUser(userId));
        return map;
    }

    public List<Map<String, Object>> getUserChatSession(int userId) {
        List<ChatSessionEntity> chatSessionEntities = chatMessageMapper.getUserChatSessions(userId);
        if (chatSessionEntities == null)
            return null;
        List<Map<String, Object>> chatSessionMaps = new ArrayList<>();
        for (ChatSessionEntity chatSessionEntity : chatSessionEntities) {
            Map<String, Object> map = new HashMap<>();
            ShopEntity shopEntity = shopMapper.findShopById(chatSessionEntity.getShopId());
            map.put("id", chatSessionEntity.getShopId());
            map.put("avatar", shopEntity.getShopAvatar());
            map.put("name", shopEntity.getShopName());
            map.put("lastMessage", chatSessionEntity.getLastMessage());
            map.put("unread", chatSessionEntity.getUnreadByUser());
            map.put("time", DateUtil.date2String(chatSessionEntity.getLastMessageAt(), "yyyy-MM-dd HH:mm:ss"));
            chatSessionMaps.add(map);
        }
        return chatSessionMaps;
    }

    public int createChatSession(int userId, int shopId) {
        List<ChatSessionEntity> chatSessionEntities = chatMessageMapper.getUserClosedChatSessions(userId);
        if (chatSessionEntities == null) {
            ChatSessionEntity chatSessionEntity = createChatSessionEntity(userId, shopId);
            return chatMessageMapper.insertSession(chatSessionEntity);
        } else {
            for (ChatSessionEntity chatSessionEntity : chatSessionEntities) {
                if (chatSessionEntity.getShopId() == shopId) {
                    chatSessionEntity.setOpenedByUser(true);
                    return chatMessageMapper.updateOpenedByUser(chatSessionEntity);
                }
            }
            ChatSessionEntity chatSessionEntity = createChatSessionEntity(userId, shopId);
            return chatMessageMapper.insertSession(chatSessionEntity);
        }
    }

    public int deleteChatSession(int userId, int shopId) {
        ChatSessionEntity chatSessionEntity = chatMessageMapper.getChatSessionByUserIdAndShopId(userId, shopId);
        if (chatSessionEntity == null)
            return 0;
        chatSessionEntity.setOpenedByUser(false);
        return chatMessageMapper.updateOpenedByUser(chatSessionEntity);
    }

    public Map<String, Object> getChatDetail(int userId, int shopId) {
        List<ChatMessageEntity> chatMessageEntities = chatMessageMapper.getChatMessagesByUserAndShopId(userId, shopId);
        if (chatMessageEntities == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        ArrayList<Map<String, Object>> messageList = new ArrayList<>();
        for (ChatMessageEntity chatMessageEntity : chatMessageEntities) {
            Map<String, Object> messageMap = new HashMap<>();
            if (chatMessageEntity.getContentType().equals("link"))
                messageMap.put("content", parseContent(chatMessageEntity.getContent()));
            else
                messageMap.put("content", chatMessageEntity.getContent());
            boolean isSelf = chatMessageEntity.getSenderId() == userId;
            if (!isSelf) {
                chatMessageEntity.setRead(true);
                chatMessageMapper.updateIsRead(chatMessageEntity);
            }
            messageMap.put("isSelf", isSelf);
            messageMap.put("isRead", chatMessageEntity.isRead());
            messageMap.put("contentType", chatMessageEntity.getContentType());
            messageList.add(messageMap);
        }
        ChatSessionEntity chatSessionEntity = chatMessageMapper.getChatSessionByUserIdAndShopId(userId, shopId);
        if (chatSessionEntity != null) {
            chatSessionEntity.setUnreadByUser(0);
            chatMessageMapper.updateSession(chatSessionEntity);
        }
        map.put(String.valueOf(shopId), messageList);
        return map;
    }

    public Map<String, Object> parseContent(String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 尝试将内容解析为 JSON
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            // 如果不是 JSON，按键值对解析
            content = content.startsWith("{") && content.endsWith("}") ? content.substring(1, content.length() - 1) : content;
            Map<String, Object> map = new HashMap<>();
            String[] pairs = content.split(";");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    map.put(keyValue[0], keyValue[1]);
                }
            }
            return map;
        }
    }

    public int sendUserMessage(int userId, int shopId, String content, String type) {
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSenderId(userId);
        chatMessageEntity.setSenderType("user");
        chatMessageEntity.setReceiverId(shopId);
        chatMessageEntity.setReceiverType("shop");
        chatMessageEntity.setContent(content);
        if (type.equals("text"))
            chatMessageEntity.setContentType("text");
        else if (type.equals("image"))
            chatMessageEntity.setContentType("image");
        else if (type.equals("link"))
            chatMessageEntity.setContentType("link");
        chatMessageEntity.setRead(false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        chatMessageEntity.setCreatedAt(calendar.getTime());
        try {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("senderId", userId);
            jsonMessage.put("senderType", "user");
            jsonMessage.put("receiverId", shopId);
            jsonMessage.put("receiverType", "shop");
            if (type.equals("text"))
                jsonMessage.put("content", content);
            else if (type.equals("image"))
                jsonMessage.put("content", "[ 图片文件 ]");
            else if (type.equals("link"))
                jsonMessage.put("content", "[ 订单链接 ]");
            ChatMessageHandler handler = new ChatMessageHandler();
            handler.handle(String.valueOf(shopId), jsonMessage.toString());

            int re = saveMessage(chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content, chatMessageEntity.getContentType(), chatMessageEntity.getSenderType(), chatMessageEntity.getReceiverType());
            if (re > 0) {
                logger.info("[chat] 消息已存储：senderId={}, receiverId={}, content={}", chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content);
                if (type.equals("image"))
                    content = "[ 图片文件 ]";
                else if (type.equals("link"))
                    content = "[ 订单链接 ]";
                re = updateUnreadCount(chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content, chatMessageEntity.getSenderType(), chatMessageEntity.getReceiverType());
                if (re > 0) {
                    logger.info("[chat] 未读计数已更新：senderId={}, receiverId={}, content={}", chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content);
                } else {
                    logger.error("[chat] 未读计数更新失败：senderId={}, receiverId={}, content={}", chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content);
                }
            } else {
                logger.error("[chat] 消息存储失败：senderId={}, receiverId={}, content={}", chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content);
            }
            return re;
        } catch (JSONException e) {
            logger.error("[sendUserMessage] 创建消息JSON错误：userId={}, shopId={}, content={}, 错误信息={}", userId, shopId, content, e.getMessage());
        }
        return 0;
    }

    private ChatSessionEntity createChatSessionEntity(int userId, int shopId) {
        ChatSessionEntity chatSessionEntity = new ChatSessionEntity();
        chatSessionEntity.setUserId(userId);
        chatSessionEntity.setShopId(shopId);
        chatSessionEntity.setUnreadByUser(0);
        chatSessionEntity.setUnreadByShop(0);
        chatSessionEntity.setOpenedByUser(true);
        chatSessionEntity.setOpenedByShop(false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        chatSessionEntity.setLastMessageAt(calendar.getTime());
        chatSessionEntity.setLastMessage("");
        return chatSessionEntity;
    }

    public int saveMessage(int senderId, int receiverId, String content, String type, String senderType, String receiverType) {
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSenderId(senderId);
        chatMessageEntity.setSenderType(senderType);
        chatMessageEntity.setReceiverId(receiverId);
        chatMessageEntity.setReceiverType(receiverType);
        chatMessageEntity.setContent(content);
        chatMessageEntity.setContentType(type);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        chatMessageEntity.setCreatedAt(calendar.getTime());
        chatMessageEntity.setRead(false);
        if (receiverType.equals("shop")) {
            String message = "id=" + senderId + " 您收到了一条来自用户的消息\n";
            if (type.equals("text")) {
                if (content.length() > 10)
                    message += content.substring(0, 10) + "...";
                else
                    message += content;
            } else if (type.equals("image")) {
                message += "[ 图片文件 ]";
            } else if (type.equals("link")) {
                message += "[ 订单链接 ]";
            }
            sendNotificationToMerchant(receiverId, message, true);
        } else if (receiverType.equals("user")) {
            String message = "id=" + senderId + " 您收到了一条来自商家的消息\n";
            if (content.length() > 10)
                message += content.substring(0, 10) + "...";
            else
                message += content;
            sendNotificationToMerchant(receiverId, message, false);
        }
        return chatMessageMapper.insertMessage(chatMessageEntity);
    }

    public int updateUnreadCount(int senderId, int receiverId, String content, String senderType, String receiverType) {
        ChatSessionEntity chatSessionEntity = new ChatSessionEntity();
        if (senderType.equals("user"))
            chatSessionEntity.setUserId(senderId);
        else if (senderType.equals("shop"))
            chatSessionEntity.setShopId(senderId);
        if (receiverType.equals("user"))
            chatSessionEntity.setUserId(receiverId);
        else if (receiverType.equals("shop"))
            chatSessionEntity.setShopId(receiverId);
        chatSessionEntity.setLastMessage(content);
        chatSessionEntity.setLastMessageAt(new Date());
        chatSessionEntity.setOpenedByUser(true);
        chatSessionEntity.setOpenedByShop(true);
        ChatSessionEntity chatSession = chatMessageMapper.getSessionByUserIdAndShopId(chatSessionEntity.getUserId(), chatSessionEntity.getShopId());
        int result;
        if (chatSession == null) {
            if (receiverType.equals("user"))
                chatSessionEntity.setUnreadByUser(1);
            else if (receiverType.equals("shop"))
                chatSessionEntity.setUnreadByShop(1);
            result = chatMessageMapper.insertSession(chatSessionEntity);
        } else {
            if (receiverType.equals("user"))
                chatSessionEntity.setUnreadByUser(chatSession.getUnreadByUser() + 1);
            else if (receiverType.equals("shop"))
                chatSessionEntity.setUnreadByShop(chatSession.getUnreadByShop() + 1);
            result = chatMessageMapper.updateSession(chatSessionEntity);
        }
        return result;
    }

    private void sendNotificationToMerchant(int shopId, String message, boolean isUser) {
        try {
            // 构造 JSON 消息
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", "chat");
            jsonMessage.put("message", message);
            jsonMessage.put("isUser", isUser);

            // 使用通知处理器发送消息
            NotificationMessageHandler handler = new NotificationMessageHandler();
            handler.handle(String.valueOf(shopId), jsonMessage.toString());  // 传递 JSON 字符串
        } catch (JSONException e) {
            logger.error("[sendNotificationToMerchant] 创建通知消息JSON错误：orderId={}, message={}, 错误信息={}", shopId, message, e.getMessage());
        }
    }
}
