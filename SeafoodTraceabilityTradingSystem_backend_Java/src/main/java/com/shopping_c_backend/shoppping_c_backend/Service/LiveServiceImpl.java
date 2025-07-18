package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.LiveMessageEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.UserEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.LiveMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ShopMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.NotificationMessageHandler;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LiveServiceImpl {
    @Resource
    private LiveMapper liveMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ShopMapper shopMapper;
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(LiveServiceImpl.class);

    public boolean checkLiveVisibility(int shopId) {
        ShopEntity shopEntity = liveMapper.getShopInfo(shopId);
        return shopEntity != null;
    }

    public boolean isLiveVisible(int shopId) {
        ShopEntity shopEntity = liveMapper.getShopInfo(shopId);
        return shopEntity.getLivestatus().equals("open");
    }

    public Map<String, Object> getLiveInfo(int shopId) {
        ShopEntity shopEntity = liveMapper.getShopInfo(shopId);
        Map<String, Object> map = new HashMap<>();
        map.put("id", shopEntity.getShopID());
        map.put("name", shopEntity.getShopName());
        map.put("avatar", shopEntity.getShopAvatar());
        map.put("description", shopEntity.getShopDesc());
        return map;
    }

    public Result UpdateLiveStatus(int shopid, String status) {
        String message;
        if (status.equals("open")) {
            message = "liveStart";
            sendNotificationToMerchant(shopid, message);
        } else if (status.equals("close")) {
            message = "liveClose";
            sendNotificationToMerchant(shopid, message);
        }
        return liveMapper.updateLiveStatus(shopid, status) == 1 ? new Result(200) : new Result(500);
    }

    public List<Map<String, Object>> getOnlineUserList(int shopId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Integer> roomIdList = liveMapper.getOnlineUserIdByRoomId(shopId);
        for (int userId : roomIdList) {
            Map<String, Object> map = getOnlineUserInfo(userId);
            list.add(map);
        }
        return list;
    }

    private Map<String, Object> getOnlineUserInfo(int userId) {
        UserEntity userEntity = userMapper.getUserById(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("id", userEntity.getId());
        map.put("name", userEntity.getNickname());
        map.put("avatar", userEntity.getAvatar());
        return map;
    }

    public Result InsertOnlineUser(int onlineUserId, int roomId) {
        int result = liveMapper.insertLiveUser(onlineUserId, roomId);
        String message = "updateUser";
        if (result == 1)
            sendNotificationToMerchant(roomId, message);
        return result == 1 ? new Result(200) : new Result(500);
    }

    public Result DeleteOnlineUser(int onlineUserId, int roomId) {
        int result = liveMapper.deleteLiveUser(onlineUserId, roomId);
        String message = "updateUser";
        if (result == 1)
            sendNotificationToMerchant(roomId, message);
        return result == 1 ? new Result(200) : new Result(500);
    }

    public List<Map<String, Object>> getLiveMessageList(int roomId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<LiveMessageEntity> messageList = liveMapper.getLiveMessageByRoomId(roomId);
        for (LiveMessageEntity message : messageList) {
            Map<String, Object> map = getLiveMessage(message);
            list.add(map);
        }
        return list;
    }

    public Result InsertLiveMessage(int sendId, int roomId, String message, String type) {
        LiveMessageEntity liveMessageEntity = new LiveMessageEntity();
        liveMessageEntity.setSendID(sendId);
        liveMessageEntity.setRoomID(roomId);
        liveMessageEntity.setMessage(message);
        liveMessageEntity.setSendType(type);
        int result = liveMapper.insertLiveMessage(liveMessageEntity);
        String WebSocketMessage = "updateMessage";
        if (result == 1)
            sendNotificationToMerchant(roomId, WebSocketMessage);
        return result == 1 ? new Result(200) : new Result(500);
    }

    private Map<String, Object> getLiveMessage(LiveMessageEntity message) {
        Map<String, Object> map = new HashMap<>();
        if (message.getSendType().equals("user")) {
            UserEntity userEntity = userMapper.getUserById(message.getSendID());
            map.put("id", userEntity.getId());
            map.put("sender", userEntity.getNickname());
            map.put("content", message.getMessage());
            map.put("avatar", userEntity.getAvatar());
            map.put("type", "user");
        } else if (message.getSendType().equals("shop")) {
            ShopEntity shopEntity = shopMapper.findShopById(message.getRoomID());
            map.put("id", shopEntity.getShopID());
            map.put("sender", shopEntity.getShopName());
            map.put("avatar", shopEntity.getShopAvatar());
            map.put("content", message.getMessage());
            map.put("type", "shop");
        }
        return map;
    }

    private void sendNotificationToMerchant(int shopId, String message) {
        try {
            // 构造 JSON 消息
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", "live");
            jsonMessage.put("message", message);
            NotificationMessageHandler handler = new NotificationMessageHandler();
            handler.handle(String.valueOf(shopId), jsonMessage.toString());  // 传递 JSON 字符串
        } catch (Exception e) {
            logger.error("[sendNotificationToMerchant] 创建通知消息JSON错误：orderId={}, message={}, 错误信息={}", shopId, message, e.getMessage());
        }
    }
}
