package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.AttendanceEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ShopMapper;
import com.shopping_c_backend.shoppping_c_backend.Util.EmailUtil;
import com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers.NotificationMessageHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ShopUserServiceImpl {
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private EmailUtil emailUtil;
    private static final Logger logger = LoggerFactory.getLogger(ShopUserServiceImpl.class);

    public boolean isResister(String phone) {
        ShopUserEntity shopUserEntity = shopMapper.findUserByPhone(phone);
        return shopUserEntity != null;
    }

    public ShopUserEntity findUserByPhone(String phone) {
        return shopMapper.findUserByPhone(phone);
    }

    public ShopUserEntity login(String phone) {
        String key = "ShopUser_" + phone;
        ValueOperations<String, ShopUserEntity> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            ShopUserEntity shopUser = operations.get(key);
            logger.info("从缓存中获取用户信息 {}", shopUser.getPhone());
            return shopUser;
        }
        ShopUserEntity shopUserEntity = shopMapper.findUserByPhone(phone);
        if (shopUserEntity != null) {
            ShopUserEntity shopUser = shopMapper.findUserByPhone(phone);
            operations.set(key, shopUser, 5, TimeUnit.HOURS);
            logger.info("从数据库中获取用户信息 {}", shopUser.getPhone());
            return shopUser;
        }
        return null;
    }

    public int insertShopUser(ShopUserEntity shopUserEntity) {
        String key = "ShopUser_" + shopUserEntity.getPhone();
        ValueOperations<String, ShopUserEntity> valueOperations = redisTemplate.opsForValue();
        ShopUserEntity shopUser;
        int id = -1;
        if (shopUserEntity.getRole().equals("staff")) {
            shopUser = shopMapper.findDissmissedUserByPhoneAndShopId(shopUserEntity.getPhone(), shopUserEntity.getShopid());
            if (shopUser == null) {
                id = shopMapper.addUser(shopUserEntity);
            } else {
                shopUser.setStatus("未审核");
                shopUser.setEmail(shopUserEntity.getEmail());
                shopUser.setNickname(shopUserEntity.getNickname());
                shopMapper.updateDissmissedStaff(shopUser);
                id = shopUser.getId();
            }
        } else if (shopUserEntity.getRole().equals("merchant"))
            id = shopMapper.addUser(shopUserEntity);
        shopUser = new ShopUserEntity();
        if (id > -1) {
            shopUser = shopMapper.findUserByPhoneAndShopId(shopUserEntity.getPhone(), shopUserEntity.getShopid());
        }
        if (shopUser != null) {
            if (Objects.equals(shopUserEntity.getRole(), "staff")) {
                String message = String.format("新员工 %s 申请加入您的店铺", shopUser.getNickname());
                sendNotificationToMerchant(shopUser.getShopid(), message);
                String AuditStaffListKey = "AuditShopStaffList_" + shopUser.getShopid();
                deleteRedisCache(AuditStaffListKey);
                getAuditShopStaffInfo(shopUser.getShopid());
            }
            valueOperations.set(key, shopUser, 5, TimeUnit.HOURS);
            logger.info("写入缓存成功 {}", shopUser.getPhone());
            return 1;
        }
        return 0;
    }

    private void sendNotificationToMerchant(int shopId, String message) {
        try {
            // 构造 JSON 消息
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put("type", "new_employee");
            jsonMessage.put("message", message);

            // 使用通知处理器发送消息
            NotificationMessageHandler handler = new NotificationMessageHandler();
            handler.handle(String.valueOf(shopId), jsonMessage.toString());  // 传递 JSON 字符串
        } catch (JSONException e) {
            logger.error("[sendNotificationToMerchant] 创建通知消息JSON错误：orderId={}, message={}, 错误信息={}", shopId, message, e.getMessage());
        }
    }

    public List<ShopUserEntity> getShopStaffInfo(int shopId) {
        String key = "ShopStaffList_" + shopId;
        ValueOperations<String, List<ShopUserEntity>> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<ShopUserEntity> shopUsers = operations.get(key);
            logger.info("从缓存中获取店铺员工列表 {}", shopId);
            return shopUsers;
        } else {
            List<ShopUserEntity> shopUsers = shopMapper.getShopUsersByShopId(shopId);
            logger.info("从数据库中获取店铺员工列表 {}", shopId);
            logger.info("缓存店铺员工列表 {}", shopId);
            operations.set(key, shopUsers, 5, TimeUnit.HOURS);
            return shopUsers;
        }
    }

    public List<ShopUserEntity> getAuditShopStaffInfo(int shopId) {
        String key = "AuditShopStaffList_" + shopId;
        ValueOperations<String, List<ShopUserEntity>> operations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<ShopUserEntity> shopUsers = operations.get(key);
            logger.info("从缓存中获取店铺待审核员工列表 {}", shopId);
            return shopUsers;
        } else {
            List<ShopUserEntity> shopUsers = shopMapper.getAuditShopUsersByShopId(shopId);
            logger.info("从数据库中获取待审核店铺员工列表 {}", shopId);
            logger.info("缓存店铺待审核员工列表 {}", shopId);
            operations.set(key, shopUsers, 5, TimeUnit.HOURS);
            return shopUsers;
        }
    }

    public int addStaff(int shopid, int id, Boolean result) {
        int re;
        String key;
        ShopUserEntity shopUserEntity = shopMapper.findUserById(id);
        ShopEntity shopEntity = shopMapper.findShopById(shopid);
        if (result) {
            re = shopMapper.addStaff(id);
            if (re > 0) {
                key = "ShopStaffList_" + shopid;
                deleteRedisCache(key);
                getShopStaffInfo(shopid);
                key = "AuditShopStaffList_" + shopid;
                deleteRedisCache(key);
                getAuditShopStaffInfo(shopid);
                String subject = "员工申请通过通知";
                emailUtil.sendStaffEmail(shopUserEntity.getEmail(), shopEntity.getShopName(), subject, "正常");
            }
        } else {
            re = shopMapper.deleteAuditStaff(id);
            if (re > 0) {
                key = "AuditShopStaffList_" + shopid;
                deleteRedisCache(key);
                getAuditShopStaffInfo(shopid);
                String subject = "员工申请未通过通知";
                emailUtil.sendStaffEmail(shopUserEntity.getEmail(), shopEntity.getShopName(), subject, "拒绝");
            }
        }
        return re;
    }

    public int dismissStaff(int shopid, int id) {
        int re = shopMapper.dismissStaff(id);
        ShopUserEntity shopUserEntity = shopMapper.findUserById(id);
        ShopEntity shopEntity = shopMapper.findShopById(shopid);
        if (re > 0) {
            String key = "ShopStaffList_" + shopid;
            deleteRedisCache(key);
            getShopStaffInfo(shopid);
            String subject = "员工离职通知";
            emailUtil.sendStaffEmail(shopUserEntity.getEmail(), shopEntity.getShopName(), subject, "离职");
        }
        return re;
    }

    public List<Map<String, Object>> getAttendanceByUserId(int userId) {
        List<AttendanceEntity> attendanceEntities = shopMapper.getAttendanceByUserId(userId);
        List<Map<String, Object>> attendanceList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (AttendanceEntity attendanceEntity : attendanceEntities) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", sdf.format(attendanceEntity.getDate()));
            attendanceList.add(map);
        }
        return attendanceList;
    }

    public int signIn(int userId, int shopId) {
        List<Integer> userIds = shopMapper.getStaffIdsByShopId(shopId);
        if (userIds.contains(userId)) {
            AttendanceEntity attendanceEntity = new AttendanceEntity();
            attendanceEntity.setUserId(userId);
            attendanceEntity.setDate(new Date());
            attendanceEntity.setStatus("已签到");
            attendanceEntity.setCreateTime(new Date());
            return shopMapper.addAttendance(attendanceEntity);
        } else
            return 0;
    }

    private void deleteRedisCache(String key) {
        logger.info("删除缓存 {}", key);
        ValueOperations<String, Map<String, Object>> valueOperations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            valueOperations.getOperations().delete(key);
            logger.info("删除已缓存商品信息缓存:{}", key);
        }
    }


}
