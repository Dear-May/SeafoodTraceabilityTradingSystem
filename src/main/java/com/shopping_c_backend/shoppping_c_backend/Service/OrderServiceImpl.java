package com.shopping_c_backend.shoppping_c_backend.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shopping_c_backend.shoppping_c_backend.Entity.*;
import com.shopping_c_backend.shoppping_c_backend.Mapper.*;
import com.shopping_c_backend.shoppping_c_backend.Util.NetworkUtil;
import com.shopping_c_backend.shoppping_c_backend.Util.TokenUtil;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.NotificationMessageHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl {
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private GoodMapper goodMapper;
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    @Lazy
    private BucketServiceImpl bucketService;
    @Resource
    @Lazy
    private ShopServiceImpl shopService;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public String storeGoodsInRedis(Map<String, Object> data, int userId) {
        String token_userId = "Order_" + userId;
        String jwtToken = TokenUtil.token((String) data.get("goodId"), token_userId);
        String key = "Token_" + jwtToken;
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(data);
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, formatData(list), 10, TimeUnit.MINUTES);
        return jwtToken;
    }

    public String storeGoodsInRedis(List<Map<String, Object>> data, int userId) {
        String token_userId = "Order_" + userId;
        String jwtToken = TokenUtil.token((String) data.get(0).get("goodId"), token_userId);
        String key = "Token_" + jwtToken;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, formatData(data), 10, TimeUnit.MINUTES);
        return jwtToken;
    }

    public List<Map<String, Object>> getGoodsFromRedis(String jwtToken) {
        String key = "Token_" + jwtToken;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            return valueOperations.get(key);
        }
        return null;
    }

    public int changeGoodNumber(String jwtToken, int number, String specId) {
        String key = "Token_" + jwtToken;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (!hasKey) {
            return 0;
        }
        List<Map<String, Object>> list = getGoodsFromRedis(jwtToken);
        for (Map<String, Object> map : list) {
            List<Map<String, Object>> goodsList = (List<Map<String, Object>>) map.get("goods");
            if (goodsList != null) {
                for (Map<String, Object> good : goodsList) {
                    Object specIdFromMap = good.get("specId");
                    if (specIdFromMap != null && specIdFromMap.equals(specId)) {
                        if (number > 10 || number <= 0) {
                            return -1;
                        }
                        int bucketId = 0;
                        boolean hasBucketId = false;
                        try {
                            bucketId = (Integer) good.get("bucketId");
                            hasBucketId = true;
                        } catch (Exception ignored) {
                        }
                        good.put("specNumber", number);
                        valueOperations.set(key, list, 10, TimeUnit.MINUTES); // 更新 Redis 数据
                        logger.info("更新 Redis 数据-{}", key);
                        if (hasBucketId) {
                            int userId = bucketService.getUserId(bucketId);
                            bucketService.updateBucketNumber(bucketId, number, userId);
                        }
                        return 1;
                    }
                }
            }
        }
        return -1;
    }

    public String submitOrder(int userId, String jwtToken, int addressId) {
        String token_userId = "Order_" + userId;
        String token = TokenUtil.token(token_userId, token_userId);
        String key = "Order_" + token;
        ValueOperations<String, List<OrderEntity>> valueOperations = redisTemplate.opsForValue();
        List<OrderEntity> orderEntities = new ArrayList<>();
        List<Map<String, Object>> goodsList = getGoodsFromRedis(jwtToken);
        List<Integer> buckleIds = new ArrayList<>();
        if (goodsList != null && !goodsList.isEmpty()) {
            for (Map<String, Object> good : goodsList) {
                String OrderId = generateOrderId();
                int index = 1;
                double totalPrice = 0;
                List<Map<String, Object>> goods = (List<Map<String, Object>>) good.get("goods");
                List<OrderItemEntity> orderItemEntities = new ArrayList<>();
                for (Map<String, Object> goodItem : goods) {
                    OrderItemEntity orderItemEntity = createOrderItem(goodItem, index, OrderId);
                    orderItemEntities.add(orderItemEntity);
                    if (goodItem.get("bucketId") != null) {
                        buckleIds.add(Integer.parseInt(goodItem.get("bucketId").toString()));
                    }
                    totalPrice += orderItemEntity.getSpecprice() * orderItemEntity.getSpecnumber();
                    index++;
                }
                OrderEntity orderEntity = createOrder(userId, OrderId, addressId, good, totalPrice);
                orderMapper.insertOrder(orderEntity);
                orderEntities.add(orderEntity);
                for (OrderItemEntity orderItemEntity : orderItemEntities)
                    orderMapper.insertOrderItem(orderItemEntity);
                redisTemplate.delete("Token_" + jwtToken);
                logger.info("删除缓存中的key-----------> {}", key);
                valueOperations.set(key, orderEntities, 1, TimeUnit.HOURS);
                logger.info("写入缓存-{}", key);
                deleteOrderByOrderIdInRedis(userId);
                deleteShopOrderInRedis(orderEntity.getShopid(), "null");
                getOrderByUserId(userId);
            }
            for (Integer bucketId : buckleIds)
                bucketService.deleteBucketByBucketId(bucketId);
            return token;
        }
        return null;
    }

    public String submitOrder(int userId, String orderId) {
        String token_userId = "Order_" + userId;
        String token = TokenUtil.token(token_userId, token_userId);
        String key = "Order_" + token;
        ValueOperations<String, List<OrderEntity>> valueOperations = redisTemplate.opsForValue();
        List<OrderEntity> orderEntities = new ArrayList<>();
        OrderEntity orderEntity = orderMapper.getOrderInfoByOrderid(orderId);
        if (orderEntity != null) {
            orderEntities.add(orderEntity);
            valueOperations.set(key, orderEntities, 1, TimeUnit.HOURS);
            logger.info("写入缓存-{}", key);
            return token;
        }
        return null;
    }

    public int changeOrderStatus(String Token, String status) {
        String key = "Order_" + Token;
        ValueOperations<String, List<OrderEntity>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        int result = 0;
        if (hasKey) {
            List<OrderEntity> orderEntities = valueOperations.get(key);
            if (orderEntities != null) {
                for (OrderEntity orderEntity : orderEntities) {
                    orderEntity.setStatus(status);
                    result = orderMapper.updateOrder(orderEntity);
                }
            }
            if (status.equals("已支付")) {
                if (orderEntities != null) {
                    for (OrderEntity orderEntity : orderEntities) {
                        String orderId = orderEntity.getOrderid();
                        String message = String.format("订单号 %s 已支付，请及时处理", orderId);
                        sendNotificationToMerchant(orderEntity.getShopid(), message, "order");
                        String keyShop = "ShopOrderInfo_" + orderEntity.getShopid();
                        ValueOperations<String, List<Map<String, Object>>> valueOperationsShop = redisTemplate.opsForValue();
                        boolean hasKeyShop = redisTemplate.hasKey(keyShop);
                        if (hasKeyShop) {
                            redisTemplate.delete(keyShop);
                            logger.info("删除缓存中的key-----------> {}", keyShop);
                        }
                    }
                }
            }
            deleteOrderByOrderIdInRedis(orderEntities.get(0).getUserid());
            deleteShopOrderInRedis(orderEntities.get(0).getShopid(), "null");
            getOrderByUserId(orderEntities.get(0).getUserid());
            return result;
        }
        return 0;
    }

    private void sendNotificationToMerchant(int shopId, String message, String type) {
        try {
            // 构造 JSON 消息
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", type);
            jsonMessage.put("message", message);

            // 使用通知处理器发送消息
            NotificationMessageHandler handler = new NotificationMessageHandler();
            handler.handle(String.valueOf(shopId), jsonMessage.toString());  // 传递 JSON 字符串
        } catch (JSONException e) {
            logger.error("[sendNotificationToMerchant] 创建通知消息JSON错误：orderId={}, message={}, 错误信息={}", shopId, message, e.getMessage());
        }
    }

    public int changeOrderStatus(int userId, String orderId, String status) {
        OrderEntity orderEntity = orderMapper.getOrderInfoByOrderid(orderId);
        int result = 0;
        if (orderEntity != null && orderEntity.getUserid() == userId) {
            orderEntity.setStatus(status);
            result = orderMapper.updateOrder(orderEntity);
            if (result == 1) {
                deleteOrderByOrderIdInRedis(userId);
                deleteShopOrderInRedis(orderEntity.getShopid(), "null");
                getOrderByUserId(userId);
            }
        }
        return result;
    }

    public List<Map<String, Object>> getOrderByUserId(int userId) {
        String username = userMapper.getUserNameById(userId);
        String key = "OrderList_" + username;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            logger.info("从缓存中获取数据-{}", key);
            return valueOperations.get(key);
        } else {
            List<OrderEntity> orderEntities = orderMapper.getOrderInfoByUserid(userId);
            List<Map<String, Object>> list = new ArrayList<>();
            for (OrderEntity orderEntity : orderEntities) {
                Map<String, Object> map = new HashMap<>();
                map.put("orderId", orderEntity.getOrderid());
                map.put("shopId", orderEntity.getShopid());
                map.put("shopName", shopMapper.findShopById(orderEntity.getShopid()).getShopName());
                map.put("address", addressMapper.getAddressById(orderEntity.getAddressid()));
                map.put("totalPrice", orderEntity.getTotalprice());
                map.put("status", orderEntity.getStatus());
                map.put("createTime", orderEntity.getInsertdate());
                map.put("updateTime", orderEntity.getUpdatedate());
                List<OrderItemEntity> orderItemEntities = orderMapper.getOrderItemByOrderId(orderEntity.getOrderid());
                map.put("orderItems", orderItemEntities);
                list.add(map);
            }
            valueOperations.set(key, list, 5, TimeUnit.HOURS);
            return list;
        }
    }

    public List<Map<String, Object>> getCommentByUserId(int userid) {
        String username = userMapper.getUserNameById(userid);
        String key = "CommentList_" + username;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            logger.info("从缓存中获取数据-{}", key);
            return valueOperations.get(key);
        } else {
            List<OrderEntity> orderEntities = orderMapper.getCommentedOrderByUserid(userid);
            List<Map<String, Object>> list = new ArrayList<>();
            for (OrderEntity orderEntity : orderEntities) {
                Map<String, Object> map = new HashMap<>();
                CommentEntity commentEntity = orderMapper.getCommentByOrderId(orderEntity.getOrderid());
                List<OrderItemEntity> orderItemEntities = orderMapper.getOrderItemByOrderId(orderEntity.getOrderid());
                if (commentEntity != null) {
                    map.put("commentId", commentEntity.getCommentid());
                    map.put("orderId", orderEntity.getOrderid());
                    map.put("rate", commentEntity.getRate());
                    map.put("content", commentEntity.getText());
                    map.put("images", commentEntity.getImages());
                    map.put("time", commentEntity.getUpdatetime());
                    map.put("shopId", orderEntity.getShopid());
                    map.put("shopName", shopMapper.findShopById(orderEntity.getShopid()).getShopName());
                    map.put("storeAvatar", shopMapper.findShopById(orderEntity.getShopid()).getShopAvatar());
                    map.put("goodId", orderItemEntities.get(0).getGood_id());
                    map.put("goodName", orderItemEntities.get(0).getGoodname());
                    map.put("totalPrice", orderEntity.getTotalprice());
                }
                list.add(map);
            }
            logger.info("写入缓存-{}", key);
            valueOperations.set(key, list, 5, TimeUnit.HOURS);
            return list;
        }
    }

    public List<Map<String, Object>> getCommentByGoodId(String goodId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<OrderItemEntity> orderItemEntities = orderMapper.getOrderItemByGoodId(goodId);
        if (orderItemEntities != null) {
            for (OrderItemEntity orderItemEntity : orderItemEntities) {
                OrderEntity orderEntity = orderMapper.getOrderInfoByOrderid(orderItemEntity.getOrderid());
                if (orderEntity != null) {
                    CommentEntity commentEntity = orderMapper.getCommentByOrderId(orderItemEntity.getOrderid());
                    if (commentEntity != null) {
                        UserEntity userEntity = userMapper.getUserById(orderEntity.getUserid());
                        Map<String, Object> map = new HashMap<>();
                        map.put("commentId", commentEntity.getCommentid());
                        map.put("userId", orderEntity.getUserid());
                        map.put("userName", userEntity.getNickname());
                        map.put("userAvatar", userEntity.getAvatar());
                        map.put("commentText", commentEntity.getText());
                        map.put("rate", commentEntity.getRate());
                        map.put("images", commentEntity.getImages());
                        map.put("time", commentEntity.getUpdatetime());
                        map.put("specname", orderItemEntity.getSpecname());
                        list.add(map);
                    }
                }
            }
        }
        return list;
    }

    public int insertComment(String orderId, String content, float rate) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setOrderid(orderId);
        commentEntity.setText(content);
        commentEntity.setRate(rate);
        commentEntity.setStatus("已发布");
        int result = orderMapper.insertComment(commentEntity);
        if (result == 1) {
            return orderMapper.getCommentIdByOrderId(orderId);
        }
        return 0;
    }

    public int deleteComment(int userId, int commentId) {
        CommentEntity commentEntity = orderMapper.getCommentByCommentId(commentId);
        if (commentEntity != null) {
            commentEntity.setStatus("已删除");
            int result = orderMapper.updateComment(commentEntity);
            if (result == 1) {
                deleteCommentInRedis(userId);
                getCommentByUserId(userId);
                return 1;
            }
        }
        return 0;
    }

    public int updateCommentImages(String orderId, String images) {
        JSONArray jsonArray = JSON.parseArray(images);
        CommentEntity commentEntity = orderMapper.getCommentByOrderId(orderId);
        if (commentEntity != null)
            commentEntity.setImages(jsonArray);
        return orderMapper.updateCommentImage(commentEntity);
    }

    public void deleteOrderByOrderIdInRedis(int userId) {
        String key = "OrderList_" + userMapper.getUserNameById(userId);
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            redisTemplate.delete(key);
            logger.info("删除缓存中的key-----------> {}", key);
        }
    }

    public void deleteCommentInRedis(int userId) {
        String key = "CommentList_" + userMapper.getUserNameById(userId);
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            redisTemplate.delete(key);
            logger.info("删除缓存中的key-----------> {}", key);
        }
    }

    public Map<String, Integer> getOrderCountByUserId(int userId) {
        Map<String, Integer> result = new HashMap<>();
        result.put("all", orderMapper.countByUserid(userId));
        result.put("unpaid", orderMapper.countByUseridAndStatus(userId, "未支付"));
        result.put("paid", orderMapper.countByUseridAndStatus(userId, "已支付"));
        result.put("delivered", orderMapper.countByUseridAndStatus(userId, "已发货"));
        result.put("evaluating", orderMapper.countByUseridAndStatus(userId, "待评价"));
        result.put("refunding", orderMapper.countByUseridAndStatus(userId, "退款"));
        result.put("cancelled", orderMapper.countByUseridAndStatus(userId, "已取消"));
        return result;
    }

    private String generateOrderId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datePart = sdf.format(new Date());
        String uuidPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        return datePart + uuidPart;
    }

    private OrderItemEntity createOrderItem(Map<String, Object> good, int index, String orderId) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderid(orderId);
        orderItemEntity.setOrderitemid(orderId + "_" + index);
        orderItemEntity.setGood_id(good.get("goodId").toString());
        orderItemEntity.setSpec_id(good.get("specId").toString());
        orderItemEntity.setGoodname(good.get("goodName").toString());
        orderItemEntity.setSpecname(good.get("specName").toString());
        orderItemEntity.setSpecprice(Double.parseDouble(good.get("specPrice").toString()));
        orderItemEntity.setSpecnumber(Integer.parseInt(good.get("specNumber").toString()));
        orderItemEntity.setSpecimg(good.get("specImage").toString());
        return orderItemEntity;
    }

    private OrderEntity createOrder(int userId, String orderId, int addressId, Map<String, Object> good, double totalPrice) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderid(orderId);
        orderEntity.setUserid(userId);
        orderEntity.setShopid((Integer) good.get("shopId"));
        orderEntity.setAddressid(addressId);
        orderEntity.setTotalprice(totalPrice);
        orderEntity.setStatus("未支付");
        orderEntity.setRemark((String) good.get("remark"));
        return orderEntity;
    }

    public String generatePaymentQRCode(String orderId) {
        String localIp = NetworkUtil.getLocalIpAddress();
        String paymentUrl = "http://" + localIp + ":8888/pay/paygoods?orderId=" + orderId;
        try {
            ByteArrayOutputStream stream = createQRCode(paymentUrl, 300, 300);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(stream.toByteArray());
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR Code", e);
        }
    }

    private ByteArrayOutputStream createQRCode(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", stream);
        return stream;
    }

    private List<Map<String, Object>> formatData(List<Map<String, Object>> data) {
        Map<String, Map<String, Object>> shopDataMap = new HashMap<>();
        for (Map<String, Object> item : data) {
            Integer shopId = goodMapper.getShopID((String) item.get("goodId"));
            ShopEntity shopEntity = shopMapper.findShopById(shopId);
            if (shopEntity == null) {
                continue;
            }
            GoodEntity goodEntity = goodMapper.getGoodByShopId((String) item.get("goodId"), shopId);
            if (goodEntity != null) {
                Map<String, Object> shopData = shopDataMap.get(String.valueOf(shopId));
                if (shopData == null) {
                    // 初始化 shopData
                    shopData = new HashMap<>();
                    shopData.put("shopId", shopId);
                    shopData.put("shopName", shopEntity.getShopName());
                    shopData.put("goods", new ArrayList<Map<String, Object>>());
                    shopData.put("remark", "");
                    shopDataMap.put(String.valueOf(shopId), shopData);
                }
                List<Map<String, Object>> goodsList = (List<Map<String, Object>>) shopData.get("goods");
                goodsList.add(item);
            }
        }
        return new ArrayList<>(shopDataMap.values());
    }

    public String addReturnInfo(String orderId, String reason) {
        ReturnGoodEntity returnGoodEntity = new ReturnGoodEntity();
        String returnId = generateOrderId();
        returnGoodEntity.setReturnId(returnId);
        returnGoodEntity.setOrderId(orderId);
        returnGoodEntity.setText(reason);
        returnGoodEntity.setUpdateTime(new Date());
        returnGoodEntity.setInsertTime(new Date());
        OrderEntity orderEntity = orderMapper.getOrderInfoByOrderid(orderId);
        returnGoodEntity.setOriginalStatus(orderEntity.getStatus());
        orderEntity.setStatus("等待商家退款");
        int result = orderMapper.updateOrder(orderEntity);
        if (result > 0) {
            String message = String.format("订单号 %s 申请退货，请及时处理。", orderId);
            sendNotificationToMerchant(orderEntity.getShopid(), message, "return");
        }
        result = orderMapper.insertReturnGood(returnGoodEntity);
        if (result > 0) {
            deleteOrderByOrderIdInRedis(orderEntity.getUserid());
            deleteShopOrderInRedis(orderEntity.getShopid(), "return");
            getOrderByUserId(orderEntity.getUserid());
        }
        return result > 0 ? returnId : null;
    }

    public int updateReturnImg(String returnId, String images) {
        JSONArray jsonArray = JSON.parseArray(images);
        ReturnGoodEntity returnGoodEntity = orderMapper.getReturnGoodByReturnId(returnId);
        returnGoodEntity.setImages(jsonArray);
        OrderEntity orderEntity = orderMapper.getOrderInfoByOrderid(returnId);
        deleteShopOrderInRedis(orderEntity.getShopid(), "return");
        return orderMapper.updateReturnGoodImage(returnGoodEntity);
    }

    public List<Map<String, Object>> getChatOrderList(int userid, int shopid) {
        List<Map<String, Object>> chatOrderList = new ArrayList<>();
        List<OrderEntity> orderEntities = orderMapper.getOrderInfoByUseridAndShopId(userid, shopid);
        for (OrderEntity orderEntity : orderEntities) {
            Map<String, Object> chatOrder = new HashMap<>();
            chatOrder.put("id", orderEntity.getOrderid());
            chatOrder.put("price", orderEntity.getTotalprice());
            List<OrderItemEntity> orderItemEntity = orderMapper.getOrderItemByOrderId(orderEntity.getOrderid());
            chatOrder.put("name", orderItemEntity.get(0).getGoodname());
            chatOrder.put("image", orderItemEntity.get(0).getSpecimg());
            chatOrderList.add(chatOrder);
        }
        return chatOrderList;
    }

    private void deleteShopOrderInRedis(int shopId, String type) {
        String key = "ShopOrderInfo_" + shopId;
        ValueOperations<String, Map<String, Object>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            valueOperations.getOperations().delete(key);
            logger.info("删除已缓存商品信息缓存:{}", key);
            shopService.getShopOrderInfo(shopId);
        }
        if (type.equals("return")) {
            key = "ShopReturnOrderInfo_" + shopId;
            hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                valueOperations.getOperations().delete(key);
                logger.info("删除已缓存退货信息缓存:{}", key);
            }
        }
    }

}
