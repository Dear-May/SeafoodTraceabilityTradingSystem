package com.shopping_c_backend.module.chat;

import java.util.List;
import java.util.Map;

public interface ChatMessageService {
    Map<String, Object> getUnreadMessageCount(int userId);
    List<Map<String, Object>> getUserChatSession(int userId);
    Map<String, Object> getMerchantChatSession(int shopId);
    int createChatSession(int userId, int shopId);
    int deleteChatSession(int userId, int shopId);
    Map<String, Object> getChatDetail(int userId, int shopId);
    int closeChatSession(int userId, int shopId);
    int saveMessage(int senderId, int receiverId, String content, String type, String senderType, String receiverType);
    int updateUnreadCount(int senderId, int receiverId, String content, String senderType, String receiverType);
}
