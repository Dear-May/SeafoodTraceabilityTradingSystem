package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.*;
import com.shopping_c_backend.shoppping_c_backend.Mapper.*;
import com.shopping_c_backend.shoppping_c_backend.Util.AliOSSUtil;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.ChatMessageHandler;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class ShopServiceImpl {
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private GoodMapper goodMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ChatMessageMapper chatMessageMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    @Lazy
    GoodServiceImpl goodService;
    @Resource
    @Lazy
    OrderServiceImpl orderService;
    @Resource
    @Lazy
    private ChatMessageServiceImpl chatMessageService;
    private static final Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);

    public ShopEntity findById(int id) {
        return shopMapper.findShopById(id);
    }

    public boolean findFollowShop(int shopId, int userId) {
        return shopMapper.isFollowed(shopId, userId) != null;
    }

    public int followShop(int shopId, int userId) {
        return shopMapper.followShop(shopId, userId);
    }

    public int unfollowShop(int shopId, int userId) {
        return shopMapper.unfollowShop(shopId, userId);
    }

    public ShopEntity createShop(ShopEntity shopEntity) {
        int result = shopMapper.addShop(shopEntity);
        int shopId = 0;
        if (result > 0)
            shopId = shopEntity.getShopID();
        if (shopId > 0)
            return shopMapper.findShopById(shopId);
        return null;
    }

    public int updateShopInfo(ShopEntity shopEntity) {
        return shopMapper.updateShop(shopEntity);
    }

    public int addReviewLincese(ReviewLicenseEntity reviewLicenseEntity) {
        return shopMapper.addReviewLicense(reviewLicenseEntity);
    }

    public ReviewLicenseEntity findReviewLicenseById(String id) {
        return shopMapper.findReviewLicenseById(id);
    }

    public int updateReviewLicense(ReviewLicenseEntity reviewLicenseEntity) {
        return shopMapper.updateReviewLicense(reviewLicenseEntity);
    }

    public boolean isAccessShop(int shopId) {
        ShopEntity shopEntity = shopMapper.findShopById(shopId);
        if (shopEntity == null) return false;
        return shopEntity.getStatus().equals("正常");
    }

    public List<Map<String, Object>> getCardInfo(int shopId) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("title", "今日支付订单");
        map.put("amount", shopMapper.countTodayOrder(shopId));
        result.add(map);
        map = new HashMap<>();
        map.put("title", "今日退货订单");
        map.put("amount", shopMapper.countTodayReturnedOrders(shopId));
        result.add(map);
        map = new HashMap<>();
        map.put("title", "待发货订单");
        map.put("amount", shopMapper.countPendingOrders(shopId));
        result.add(map);
        map = new HashMap<>();
        map.put("title", "今日未支付订单");
        map.put("amount", shopMapper.countTodayUnpaidOrders(shopId));
        result.add(map);
        map = new HashMap<>();
        map.put("title", "今日员工考勤人数");
        int count = shopMapper.countTodayAttendance(shopId);
        map.put("amount", count);
        result.add(map);
        map = new HashMap<>();
        map.put("title", "今日未考勤员工人数");
        map.put("amount", shopMapper.countShopUsers(shopId) - count - 1);
        result.add(map);
        map = new HashMap<>();
        map.put("title", "本月支付订单");
        map.put("amount", shopMapper.countThisMonthOrder(shopId));
        result.add(map);
        map = new HashMap<>();
        map.put("title", "粉丝数");
        map.put("amount", shopMapper.countFans(shopId));
        result.add(map);
        return result;
    }

    public List<Map<String, Object>> getProductInfo(int shopId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<GoodEntity> goodEntities = goodMapper.getGoodsByShopIdOrderByUploadTime(shopId);
        for (GoodEntity goodEntity : goodEntities) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", goodEntity.getGoodName());
            Double count = shopMapper.countTodayOrderItem(goodEntity.getGoodID());
            double total = count != null ? count : 0.0;
            map.put("today", total);
            count = shopMapper.countThisMonthOrderItem(goodEntity.getGoodID());
            total = count != null ? count : 0.0;
            map.put("month", total);
            count = shopMapper.countTotalOrderItem(goodEntity.getGoodID());
            total = count != null ? count : 0.0;
            map.put("total", total);
            result.add(map);
        }
        result.sort((o1, o2) -> {
            double total1 = (double) o1.get("total");
            double total2 = (double) o2.get("total");
            return (int) (total2 - total1);
        });
        if (result.size() > 6) {
            return result.subList(0, 6);
        } else
            return result;
    }

    public Map<String, Object> getProductTrendInfo(int shopId) {
        Map<String, Object> map = new HashMap<>();
        // Tooltip
        map.put("tooltip", new HashMap<>());
        // 获取商品数据
        List<GoodEntity> goodEntities = goodMapper.getGoodsByShopIdOrderByUploadTime(shopId);
        // 设置 legend 数据
        Map<String, Object> legend = new HashMap<>();
        List<String> legendData = new ArrayList<>();
        for (GoodEntity goodEntity : goodEntities) {
            legendData.add(goodEntity.getGoodName());
        }
        legend.put("data", legendData);
        map.put("legend", legend);
        // 设置 yAxis
        Map<String, Object> yAxis = new HashMap<>();
        yAxis.put("type", "value");
        map.put("yAxis", yAxis);
        // 设置 xAxis
        Set<String> xAxisItem = new LinkedHashSet<>(); // 使用 Set 去重并保持顺序
        List<Map<String, Object>> series = new ArrayList<>();
        for (GoodEntity goodEntity : goodEntities) {
            Map<String, Object> goodMap = new HashMap<>();
            goodMap.put("name", goodEntity.getGoodName());
            goodMap.put("type", "line");
            // 获取每周数据
            List<Map<String, Object>> WeekData = shopMapper.getWeeklyTotalPrice(goodEntity.getGoodID());
            List<Double> dataList = new ArrayList<>();
            for (Map<String, Object> weekData : WeekData) {
                // 转换 total_price 为 double
                BigDecimal bigDecimal = (BigDecimal) weekData.get("total_price");
                double price = bigDecimal != null ? bigDecimal.doubleValue() : 0.0;

                // 获取周数并去重
                String weekNumber = "第" + weekData.get("week_number") + "周";
                xAxisItem.add(weekNumber);

                dataList.add(price);
            }
            goodMap.put("data", dataList);
            series.add(goodMap);
        }
        // 设置 xAxis
        Map<String, Object> xAxis = new HashMap<>();
        xAxis.put("type", "category");
        xAxis.put("data", new ArrayList<>(xAxisItem)); // 将 Set 转为 List
        map.put("xAxis", xAxis);
        // 设置 series
        map.put("series", series);
        return map;
    }

    public Map<String, Object> getUserInfo(int shopId) {
        // 获取近六周的粉丝数据
        List<Map<String, Object>> fansData = shopMapper.getWeeklyFanCount(shopId);
        // 获取近六周的下单用户数据
        List<Map<String, Object>> orderUserData = shopMapper.getWeeklyOrderUserCount(shopId);
        // 构造返回数据
        Map<String, Object> chartData = new HashMap<>();
        List<String> xAxis = new ArrayList<>();
        List<Integer> fansList = new ArrayList<>();
        List<Integer> orderUserList = new ArrayList<>();
        for (Map<String, Object> data : fansData) {
            String week = "第" + data.get("week_number") + "周";
            xAxis.add(week);
            fansList.add(((Long) data.get("fan_count")).intValue());
        }
        for (Map<String, Object> data : orderUserData) {
            orderUserList.add(((Long) data.get("user_count")).intValue());
        }
        // 填充图表数据
        chartData.put("tooltip", new HashMap<>());
        chartData.put("legend", Map.of("data", List.of("新增粉丝", "下单用户")));
        chartData.put("xAxis", Map.of("type", "category", "data", xAxis));
        chartData.put("yAxis", Map.of("type", "value"));
        chartData.put("series", List.of(
                Map.of("name", "新增粉丝", "type", "bar", "data", fansList),
                Map.of("name", "下单用户", "type", "bar", "data", orderUserList)
        ));
        return chartData;
    }

    public Map<String, Object> getOrderStatusInfo(int shopId) {
        Map<String, Object> chartData = new HashMap<>();
        // 订单状态列表
        List<String> legendData = Stream.of("已取消", "未支付", "已支付", "已发货", "待收货", "已评价", "等待商家退款", "退款成功").toList();
        // 数据存储
        List<Map<String, Object>> seriesData = new ArrayList<>();
        // 查询数据库，统计每个订单状态的数量
        List<Map<String, Object>> statusCounts = shopMapper.getOrderStatusCount(shopId);
        // 填充 series 数据
        for (String status : legendData) {
            // 查找数据库中该状态的计数
            Integer count = statusCounts.stream()
                    .filter(data -> status.equals(data.get("status"))) // 根据状态匹配
                    .map(data -> ((Long) data.get("count")).intValue()) // 转换为 int 类型
                    .findFirst()
                    .orElse(0);
            // 添加到 series 数据中
            Map<String, Object> dataItem = new HashMap<>();
            dataItem.put("name", status);
            dataItem.put("value", count);
            seriesData.add(dataItem);
        }
        chartData.put("legend", Map.of(
                "orient", "horizontal",
                "bottom", 0,
                "data", legendData
        ));
        chartData.put("series", List.of(Map.of(
                "name", "订单状态分布",
                "type", "pie",
                "radius", "50%",
                "data", seriesData,
                "emphasis", Map.of(
                        "itemStyle", Map.of(
                                "shadowBlur", 10,
                                "shadowOffsetX", 0,
                                "shadowColor", "rgba(0, 0, 0, 0.5)"
                        )
                )
        )));
        chartData.put("tooltip", Map.of("trigger", "item"));
        return chartData;
    }


    public List<Map<String, Object>> getAllGoodInfo(int shopId) {
        String key = "ShopGoodInfo_" + shopId;
        ValueOperations<String, List<Map<String, Object>>> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<Map<String, Object>> resultMap = operations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return resultMap;
        } else {
            List<GoodEntity> goodEntities = goodMapper.getGoodsByShopIdOrderByUploadTime(shopId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (GoodEntity goodEntity : goodEntities) {
                Map<String, Object> map = new HashMap<>();
                map.put("goodID", goodEntity.getGoodID());
                map.put("name", goodEntity.getGoodName());
                map.put("origin", goodEntity.getLocation());
                map.put("imageUrl", goodEntity.getShowURL());
                map.put("status", goodEntity.getStatus());
                map.put("isEdited", false);
                String GoodId = goodEntity.getGoodID();
                List<SpecificationEntity> specificationEntities = goodMapper.getSpecificationByMainProductID(GoodId);
                ArrayList<Map<String, Object>> specifications = new ArrayList<>();
                for (SpecificationEntity specificationEntity : specificationEntities) {
                    Map<String, Object> specMap = getStringObjectMap(specificationEntity);
                    specifications.add(specMap);
                }
                map.put("specs", specifications);
                result.add(map);
            }
            logger.info("查询数据库获取数据:{}", key);
            logger.info("------------写入缓存-");
            //写入缓存
            operations.set(key, result, 5, TimeUnit.HOURS);
            return result;
        }
    }

    @NotNull
    private static Map<String, Object> getStringObjectMap(SpecificationEntity specificationEntity) {
        Map<String, Object> specMap = new HashMap<>();
        specMap.put("specID", specificationEntity.getSpecificationID());
        specMap.put("name", specificationEntity.getSpecName());
        specMap.put("price", specificationEntity.getPrice());
        specMap.put("stock", specificationEntity.getNumber());
        specMap.put("imageUrl", specificationEntity.getShowurl());
        specMap.put("status", specificationEntity.isStatus());
        specMap.put("isEdited", false);
        return specMap;
    }

    public int insertGoodInfo(GoodEntity goodEntity, List<SpecificationEntity> specificationEntities, MultipartFile file, List<MultipartFile> images) throws IOException {
        String lastGoodId = goodMapper.getLastGoodId(goodEntity.getShopID());
        String goodId;
        String prefix;
        AliOSSUtil aliOSSUtil = new AliOSSUtil();
        String path = "GoodFirstShow";
        if (lastGoodId == null) {
            prefix = String.format("%04d", goodEntity.getShopID());
            goodId = prefix + "0001";
        } else {
            prefix = lastGoodId.substring(0, 4);
            String lastHexPart = lastGoodId.substring(lastGoodId.length() - 4);
            int lastNumber = Integer.parseInt(lastHexPart, 16) + 1;
            String newHexPart = String.format("%04X", lastNumber);
            goodId = prefix + newHexPart;
        }
        goodEntity.setGoodID(goodId);
        String result = aliOSSUtil.upload(file, path, String.valueOf(goodEntity.getGoodID()));
        if (result != null) {
            String avatarUrl = "https://oss.yy0313.fit/" + path + "/" + goodEntity.getGoodID() + ".jpg";
            goodEntity.setShowURL(avatarUrl);
        }
        int resultGood = goodMapper.addGood(goodEntity);
        if (resultGood <= 0) return 0;
        int index = 1;
        for (SpecificationEntity specificationEntity : specificationEntities) {
            specificationEntity.setMainProductID(goodId);
            String specId = goodId + "_" + index;
            specificationEntity.setSpecificationID(specId);
            String resultSpec = aliOSSUtil.upload(images.get(index - 1), path, specId);
            if (resultSpec != null) {
                String specUrl = "https://oss.yy0313.fit/" + path + "/" + specId + ".jpg";
                specificationEntity.setShowurl(specUrl);
            }
            int resultSpecEntity = goodMapper.addSpecification(specificationEntity);
            if (resultSpecEntity <= 0) return 0;
            index++;
        }
        String key = "ShopGoodInfo_" + goodEntity.getShopID();
        deleteGoodsFormRedis(goodEntity.getShopID(), key, 1);
        return 1;
    }

    public int updateGoodInfo(GoodEntity goodEntity, String type) {
        int result = 0;
        switch (type) {
            case "goodStatus":
                if (goodEntity.getStatus())
                    result = goodMapper.updateGoodStatus(true, goodEntity.getGoodID());
                else {
                    result = goodMapper.updateGoodStatus(false, goodEntity.getGoodID());
                    result = goodMapper.updateSpecificationStatus(false, goodEntity.getGoodID());
                }
            case "goodInfo":
                result = goodMapper.updateGood(goodEntity.getGoodID(), goodEntity.getGoodName(), goodEntity.getLocation());
                break;
            default:
                break;
        }
        if (result > 0) {
            String key = "ShopGoodInfo_" + goodEntity.getShopID();
            deleteGoodsFormRedis(goodEntity.getShopID(), key, 1);
            deleteGoodFormRedis(goodEntity.getGoodID());
        }
        return result;
    }

    public int updateSpecificationInfo(SpecificationEntity specificationEntity, String type) {
        int result = 0;
        switch (type) {
            case "specStatus":
                if (specificationEntity.isStatus()) {
                    result = goodMapper.updateSpecificationStatusByID(specificationEntity.getSpecificationID(), true);
                    GoodEntity goodEntity = goodMapper.getGoodById(specificationEntity.getMainProductID());
                    if (!goodEntity.getStatus())
                        result = goodMapper.updateGoodStatus(true, goodEntity.getGoodID());
                } else {
                    result = goodMapper.updateSpecificationStatusByID(specificationEntity.getSpecificationID(), false);
                    List<SpecificationEntity> specificationEntities = goodMapper.getSpecificationByMainProductID(specificationEntity.getMainProductID());
                    boolean allStatus = true;
                    for (SpecificationEntity entity : specificationEntities) {
                        if (entity.isStatus()) {
                            allStatus = false;
                            break;
                        }
                    }
                    if (allStatus) {
                        result = goodMapper.updateGoodStatus(false, specificationEntity.getMainProductID());
                    }
                }
                break;
            case "specInfo":
                result = goodMapper.updateSpecification(specificationEntity.getSpecificationID(), specificationEntity.getSpecName(), specificationEntity.getPrice(), specificationEntity.getNumber());
                break;
            default:
                break;
        }
        if (result > 0) {
            GoodEntity goodEntity = goodMapper.getGoodById(specificationEntity.getMainProductID());
            String key = "ShopGoodInfo_" + goodEntity.getShopID();
            deleteGoodsFormRedis(goodEntity.getShopID(), key, 1);
            deleteGoodFormRedis(specificationEntity.getMainProductID());
        }
        return result;
    }

    private void deleteGoodsFormRedis(int shopId, String key, int result) {
        if (result == 1) {
            ValueOperations<String, Map<String, List<Map<String, Object>>>> valueOperations = redisTemplate.opsForValue();
            //判断redis中是否有键为key的缓存
            boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                valueOperations.getOperations().delete(key);
                logger.info("删除商店商品信息缓存:{}", key);
                getAllGoodInfo(shopId);
            }
        }
    }

    private void deleteGoodFormRedis(String goodId) {
        String key = "goodAllInfo_" + goodId;
        ValueOperations<String, Map<String, Object>> valueOperations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            valueOperations.getOperations().delete(key);
            logger.info("删除已缓存商品信息缓存:{}", key);
            goodService.getGoodAllInfo(goodId);
        }
    }

    public void deleteOrderFormRedis(int shopId, String key, int result) {
        if (result == 1) {
            ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
            //判断redis中是否有键为key的缓存
            boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                valueOperations.getOperations().delete(key);
                logger.info("删除商店订单信息缓存:{}", key);
                getShopOrderInfo(shopId);
            }
        }
    }

    public Map<String, Integer> getOrderCount(int shopId) {
        Map<String, Integer> result = new HashMap<>();
        result.put("all", orderMapper.countByShopId(shopId));
        result.put("paid", orderMapper.countByShopIdAndStatus(shopId, "已支付"));
        result.put("delivered", orderMapper.countByShopIdAndStatus(shopId, "已发货"));
        result.put("refunding", orderMapper.countByShopIdAndStatus(shopId, "退款"));
        return result;
    }

    public List<Map<String, Object>> getShopOrderInfo(int shopId) {
        String key = "ShopOrderInfo_" + shopId;
        ValueOperations<String, List<Map<String, Object>>> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<Map<String, Object>> resultMap = operations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return resultMap;
        } else {
            List<OrderEntity> orderEntities = orderMapper.getOrderInfoByShopId(shopId);
            List<Map<String, Object>> list = new ArrayList<>();
            for (OrderEntity orderEntity : orderEntities) {
                Map<String, Object> map = new HashMap<>();
                map.put("orderId", orderEntity.getOrderid());
                map.put("userId", orderEntity.getUserid());
                map.put("userName", userMapper.getUserById(orderEntity.getUserid()).getNickname());
                map.put("address", addressMapper.getAddressById(orderEntity.getAddressid()));
                map.put("totalPrice", orderEntity.getTotalprice());
                map.put("status", orderEntity.getStatus());
                map.put("createTime", orderEntity.getInsertdate());
                map.put("updateTime", orderEntity.getUpdatedate());
                List<OrderItemEntity> orderItemEntities = orderMapper.getOrderItemByOrderId(orderEntity.getOrderid());
                map.put("orderItems", orderItemEntities);
                list.add(map);
            }
            logger.info("查询数据库获取数据:{}", key);
            logger.info("------------写入缓存-");
            //写入缓存
            operations.set(key, list, 5, TimeUnit.HOURS);
            return list;
        }
    }

    public List<Map<String, Object>> getShopReturnOrderInfo(int shopId) {
        String key = "ShopReturnOrderInfo_" + shopId;
        ValueOperations<String, List<Map<String, Object>>> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<Map<String, Object>> resultMap = operations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return resultMap;
        } else {
            List<OrderEntity> orderEntities = orderMapper.getReturnOrderInfoByShopId(shopId);
            List<Map<String, Object>> list = new ArrayList<>();
            for (OrderEntity orderEntity : orderEntities) {
                Map<String, Object> map = new HashMap<>();
                map.put("orderId", orderEntity.getOrderid());
                map.put("userId", orderEntity.getUserid());
                map.put("userName", userMapper.getUserById(orderEntity.getUserid()).getNickname());
                map.put("address", addressMapper.getAddressById(orderEntity.getAddressid()));
                map.put("totalPrice", orderEntity.getTotalprice());
                String status = orderEntity.getStatus();
                if (!status.contains("退款"))
                    status = "未通过申请";
                map.put("status", status);
                map.put("createTime", orderEntity.getInsertdate());
                map.put("updateTime", orderEntity.getUpdatedate());
                List<OrderItemEntity> orderItemEntities = orderMapper.getOrderItemByOrderId(orderEntity.getOrderid());
                map.put("orderItems", orderItemEntities);
                ReturnGoodEntity returnGoodEntity = orderMapper.getReturnGoodByReturnId(orderEntity.getOrderid());
                map.put("returnGood", returnGoodEntity);
                if (returnGoodEntity.getWorkUserId() > 0) {
                    ShopUserEntity shopUserEntity = shopMapper.findUserById(returnGoodEntity.getWorkUserId());
                    map.put("workUserName", shopUserEntity.getNickname());
                } else
                    map.put("workUserName", "");
                list.add(map);
            }
            logger.info("查询数据库获取数据:{}", key);
            logger.info("------------写入缓存-");
            //写入缓存
            operations.set(key, list, 5, TimeUnit.HOURS);
            return list;
        }
    }

    public int updateOrderStatus(String orderID, String status, int shopId, int userId) {
        int result = orderService.changeOrderStatus(userId, orderID, status);
        if (result > 0) {
            String key = "ShopOrderInfo_" + shopId;
            deleteGoodsFormRedis(shopId, key, 1);
            getOrderCount(shopId);
            orderService.deleteOrderByOrderIdInRedis(userId);
            orderService.getOrderByUserId(userId);
        }
        return result;
    }

    public int changeReturnStatus(String orderID, String status, int userId) {
        ReturnGoodEntity returnGoodEntity = orderMapper.getReturnGoodByReturnId(orderID);
        OrderEntity orderEntity = orderMapper.getOrderInfoByOrderid(orderID);
        returnGoodEntity.setUpdateTime(new Date());
        returnGoodEntity.setWorkUserId(userId);
        int result = orderMapper.updateReturnGoodWorkUserId(returnGoodEntity);
        if (result > 0) {
            if (status.equals("success")) {
                orderEntity.setStatus("退款成功");
                result = orderMapper.updateOrder(orderEntity);
            } else if (status.equals("fail")) {
                orderEntity.setStatus(returnGoodEntity.getOriginalStatus());
                result = orderMapper.updateOrder(orderEntity);
            }
            orderService.deleteOrderByOrderIdInRedis(orderEntity.getUserid());
            orderService.getOrderCountByUserId(orderEntity.getUserid());
        }
        String key = "ShopReturnOrderInfo_" + orderEntity.getShopid();
        deleteRedisCache(key);
        getShopReturnOrderInfo(orderEntity.getShopid());
        return result;
    }

    public List<Map<String, Object>> getTalkList(int shopId) {
        List<ChatSessionEntity> chatSessionEntities = chatMessageMapper.getChatSessionByShopId(shopId);
        if (chatSessionEntities == null || chatSessionEntities.isEmpty())
            return null;
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatSessionEntity chatSessionEntity : chatSessionEntities) {
            Map<String, Object> map = new HashMap<>();
            UserEntity userEntity = userMapper.getUserById(chatSessionEntity.getUserId());
            map.put("id", userEntity.getId());
            map.put("nickname", userEntity.getNickname());
            map.put("avatar", userEntity.getAvatar());
            map.put("lastMessage", chatSessionEntity.getLastMessage());
            map.put("lastTime", chatSessionEntity.getLastMessageAt());
            result.add(map);
        }
        return result;
    }

    public int deleteChatSession(int userId, int shopId) {
        ChatSessionEntity chatSessionEntity = chatMessageMapper.getChatSessionByUserIdAndShopId(userId, shopId);
        if (chatSessionEntity == null)
            return 0;
        chatSessionEntity.setOpenedByShop(false);
        return chatMessageMapper.updateOpenedByShop(chatSessionEntity);
    }

    public Map<String, Object> getTalkDetail(int userId, int shopId) {
        List<ChatMessageEntity> chatMessageEntities = chatMessageMapper.getChatMessagesByUserAndShopId(userId, shopId);
        if (chatMessageEntities == null)
            return null;
        Map<String, Object> map = new HashMap<>();
        ArrayList<Map<String, Object>> messageList = new ArrayList<>();
        for (ChatMessageEntity chatMessageEntity : chatMessageEntities) {
            Map<String, Object> messageMap = new HashMap<>();
            if (chatMessageEntity.getContentType().equals("link"))
                messageMap.put("content", chatMessageService.parseContent(chatMessageEntity.getContent()));
            else
                messageMap.put("content", chatMessageEntity.getContent());
            boolean isSelf = chatMessageEntity.getSenderId() == shopId;
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
            chatSessionEntity.setUnreadByShop(0);
            chatMessageMapper.updateSession(chatSessionEntity);
        }
        map.put(String.valueOf(userId), messageList);
        return map;
    }

    public int sendMessage(int userId, int shopId, String content, String type) {
        ChatMessageEntity chatMessageEntity = new ChatMessageEntity();
        chatMessageEntity.setSenderId(shopId);
        chatMessageEntity.setSenderType("shop");
        chatMessageEntity.setReceiverId(userId);
        chatMessageEntity.setReceiverType("user");
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
            jsonMessage.put("senderId", shopId);
            jsonMessage.put("senderType", "shop");
            jsonMessage.put("receiverId", userId);
            jsonMessage.put("receiverType", "user");
            if (type.equals("text"))
                jsonMessage.put("content", content);
            else if (type.equals("image"))
                jsonMessage.put("content", "[ 图片文件 ]");
            else if (type.equals("link"))
                jsonMessage.put("content", "[ 订单链接 ]");

            ChatMessageHandler handler = new ChatMessageHandler();
            handler.handle(String.valueOf(shopId), jsonMessage.toString());

            int re = chatMessageService.saveMessage(chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content, chatMessageEntity.getContentType(), chatMessageEntity.getSenderType(), chatMessageEntity.getReceiverType());
            if (re > 0) {
                logger.info("[chat] 消息已存储：senderId={}, receiverId={}, content={}", chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content);
                if (type.equals("image"))
                    content = "[ 图片文件 ]";
                else if (type.equals("link"))
                    content = "[ 订单链接 ]";
                re = chatMessageService.updateUnreadCount(chatMessageEntity.getSenderId(), chatMessageEntity.getReceiverId(), content, chatMessageEntity.getSenderType(), chatMessageEntity.getReceiverType());
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

    private void deleteRedisCache(String key) {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            operations.getOperations().delete(key);
            logger.info("删除缓存:{}", key);
        }
    }
}
