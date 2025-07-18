package com.shopping_c_backend.shoppping_c_backend.Mapper;

import com.shopping_c_backend.shoppping_c_backend.Entity.ChatMessageEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ChatSessionEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface ChatMessageMapper {
    // 查询

    @Select("SELECT  coalesce(SUM(unreadByUser),0) FROM chat_sessions where userId = #{userId}")
    int getUnreadCountByUser(@Param("userId") int userId);

    @Select("select * from chat_messages where senderId = #{senderId} and senderType = #{senderType} and receiverId = #{receiverId} and receiverType = #{receiverType} order by createdAt desc")
    List<ChatMessageEntity> getChatMessagesBySenderAndReceiver(@Param("senderId") int senderId, @Param("senderType") String senderType, @Param("receiverId") int receiverId, @Param("receiverType") String receiverType);

    @Select(("select * from chat_sessions where userId = #{userId} and openedByUser = true order by lastMessageAt desc"))
    List<ChatSessionEntity> getUserChatSessions(@Param("userId") int userId);

    @Select("select * from chat_sessions where userId = #{userId} and openedByUser = false order by lastMessageAt desc")
    List<ChatSessionEntity> getUserClosedChatSessions(@Param("userId") int userId);

    @Select("select * from chat_sessions where userId = #{userId} and shopId = #{shopId} and openedByUser = true order by lastMessageAt desc")
    ChatSessionEntity getChatSessionByUserIdAndShopId(@Param("userId") int userId, @Param("shopId") int shopId);

    @Select("select * from chat_sessions where shopId = #{shopId} and openedByShop = true order by lastMessageAt desc")
    List<ChatSessionEntity> getChatSessionByShopId(@Param("shopId") int shopId);

    @Select("select * from  chat_sessions where userId = #{userId} and shopId = #{shopId}")
    ChatSessionEntity getSessionByUserIdAndShopId(@Param("userId") int userId, @Param("shopId") int shopId);

    @Select("select * from chat_messages where" +
            "(senderId = #{userId}  and senderType= 'user' and receiverId=#{shopId} and receiverType= 'shop') or " +
            "(senderId= #{shopId}  and senderType= 'shop' and receiverId=#{userId} and receiverType= 'user') order by createdAt")
    List<ChatMessageEntity> getChatMessagesByUserAndShopId(@Param("userId") int userId, @Param("shopId") int shopId);

    // 新增
    @Insert("insert into chat_messages (senderId, senderType, receiverId, receiverType, content, contentType, isRead, createdAt) VALUES (#{senderId}, #{senderType}, #{receiverId}, #{receiverType}, #{content}, #{contentType}, #{isRead}, now())")
    int insertMessage(ChatMessageEntity chatMessageEntity);

    @Insert("insert into chat_sessions (userId, shopId, lastMessage, lastMessageAt, unreadByUser, unreadByShop, openedByUser, openedByShop) VALUES (#{userId}, #{shopId}, #{lastMessage}, #{lastMessageAt}, #{unreadByUser}, #{unreadByShop}, #{openedByUser}, #{openedByShop})")
    int insertSession(ChatSessionEntity chatSessionEntity);

    // 更新
    @Update("update chat_sessions set lastMessage = #{lastMessage}, lastMessageAt = #{lastMessageAt}, unreadByUser = #{unreadByUser}, unreadByShop = #{unreadByShop}, openedByUser = #{openedByUser}, openedByShop = #{openedByShop} where userId = #{userId} and shopId = #{shopId}")
    int updateSession(ChatSessionEntity chatSessionEntity);

    @Update("update chat_sessions set openedByUser = #{openedByUser} where userId = #{userId} and shopId = #{shopId}")
    int updateOpenedByUser(ChatSessionEntity chatSessionEntity);

    @Update("update chat_sessions set openedByShop = #{openedByShop} where userId = #{userId} and shopId = #{shopId}")
    int updateOpenedByShop(ChatSessionEntity chatSessionEntity);

    @Update("update chat_messages set isRead = #{isRead} where id = #{id}")
    int updateIsRead(ChatMessageEntity chatMessageEntity);

}
