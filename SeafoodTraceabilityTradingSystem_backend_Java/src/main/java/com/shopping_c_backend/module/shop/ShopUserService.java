package com.shopping_c_backend.module.shop;

import java.util.List;
import java.util.Map;

public interface ShopUserService {
    List<Map<String, Object>> getShopUsers(int shopId);
    int insertShopUser(int shopId, int userId, int role);
    int deleteShopUser(int shopUserId);
    Map<String, Object> getAttendance(int shopId, String date);
    int takeAttendance(int shopUserId, String date);
    boolean isCheckedIn(int shopUserId, String date);
}
