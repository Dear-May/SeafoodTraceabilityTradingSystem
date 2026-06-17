package com.shopping_c_backend.Service;

import java.util.List;
import java.util.Map;

public interface OrderService {
    String storeGoodsInRedis(Map<String, Object> data, int userId);
    String storeGoodsInRedis(List<Map<String, Object>> data, int userId);
    List<Map<String, Object>> getGoodsFromRedis(String jwtToken);
    int changeGoodNumber(String jwtToken, int number, String specId);
    int deleteGoodInRedis(String jwtToken, String goodId, String specId);
    List<Map<String, Object>> getOrderByUserId(int userId);
    List<Map<String, Object>> getOrderByShopId(int shopId, String type);
    Map<String, Object> getOrderInfoByOrderId(String orderId);
    String addOrder(int userId, String jwtToken, int addressId, String remark);
    String addReturnInfo(String orderId, String reason);
    int updateReturnImg(String returnId, String images);
    int updateOrderStatus(String orderId, String status);
    List<Map<String, Object>> getChatOrderList(int userid, int shopid);
}
