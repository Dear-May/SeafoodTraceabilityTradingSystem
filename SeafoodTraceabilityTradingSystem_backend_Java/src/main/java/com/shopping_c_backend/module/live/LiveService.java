package com.shopping_c_backend.module.live;

import com.shopping_c_backend.common.web.Result;
import java.util.List;
import java.util.Map;

public interface LiveService {
    boolean checkLiveVisibility(int shopId);
    boolean isLiveVisible(int shopId);
    Map<String, Object> getLiveInfo(int shopId);
    Result UpdateLiveStatus(int shopId, String status);
    List<Map<String, Object>> getOnlineUserList(int shopId);
    Result InsertOnlineUser(int onlineUserId, int roomId);
    Result DeleteOnlineUser(int onlineUserId, int roomId);
    List<Map<String, Object>> getLiveMessageList(int roomId);
    Result InsertLiveMessage(int sendId, int roomId, String message, String type);
}
