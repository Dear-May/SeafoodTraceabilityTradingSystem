package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.BucketEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.SpecificationEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.BucketMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.GoodMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ShopMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
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
public class BucketServiceImpl {
    @Resource
    private BucketMapper bucketMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private GoodMapper goodMapper;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(BucketServiceImpl.class);

    public int getBucketNumber(int userId) {
        return bucketMapper.countByUserId(userId);
    }

    public List<Map<String, Object>> getBucket(int userId) {
        List<BucketEntity> bucketEntities = bucketMapper.selectByUserId(userId);
        String username = userMapper.getUserNameById(userId);
        String key = "bucket_" + username;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();

        // 判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<Map<String, Object>> cachedData = valueOperations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return cachedData;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            // 使用一个临时 Map 来按 shopName 聚合数据
            Map<String, Map<String, Object>> shopDataMap = new HashMap<>();

            for (BucketEntity bucketEntity : bucketEntities) {
                GoodEntity goodEntity = goodMapper.getGoodById(bucketEntity.getGoodid());
                ShopEntity shopEntity = shopMapper.findShopById(goodEntity.getShopID());
                SpecificationEntity specificationEntity = goodMapper.getSpecificationBySpecificationID(bucketEntity.getSpecificationid());
                String shopName = shopEntity.getShopName();

                // 如果这个店铺没有被记录，则初始化之
                if (!shopDataMap.containsKey(shopName)) {
                    Map<String, Object> shopData = new HashMap<>();
                    shopData.put("shopName", shopName);
                    shopData.put("shopId", shopEntity.getShopID());
                    shopData.put("shopSelect", false); // 添加 shopselect 属性
                    shopData.put("goods", new ArrayList<Map<String, Object>>()); // 初始化 goods
                    shopDataMap.put(shopName, shopData);
                }

                // 将商品数据加入到对应店铺的 goods 列表中
                List<Map<String, Object>> goodsList = (List<Map<String, Object>>) shopDataMap.get(shopName).get("goods");
                goodsList.add(getData(bucketEntity, goodEntity, specificationEntity));
            }

            // 将聚合后的数据转换为 List
            List<Map<String, Object>> AllData = new ArrayList<>(shopDataMap.values());
            logger.info("从数据库获取数据:{}", key);
            logger.info("缓存数据:{}", AllData);
            // 写入缓存
            valueOperations.set(key, AllData, 5, TimeUnit.HOURS);
            return AllData;
        }
    }

    public int getUserId(int bucketId) {
        BucketEntity bucketEntity = bucketMapper.selectByBucketId(bucketId);
        return bucketEntity.getUserid();
    }

    public int addBucket(int userId, String goodid, String specid, int number) {
        String username = userMapper.getUserNameById(userId);
        String key = "bucket_" + username;
        BucketEntity selectedBucket = bucketMapper.selectByGoodIdAndSpecificationId(userId, goodid, specid);
        int result;
        if (selectedBucket != null) {
            selectedBucket.setNumber(selectedBucket.getNumber() + number);
            result = bucketMapper.update(selectedBucket.getUserid(), selectedBucket.getGoodid(), selectedBucket.getSpecificationid(), selectedBucket.getNumber());
        } else {
            BucketEntity bucketEntity = new BucketEntity(userId, goodid, specid, number);
            result = bucketMapper.insert(bucketEntity);
        }
        return deleteRedisInfo(userId, key, result);
    }

    public int updateBucket(int userId, String goodid, String specid, int number) {
        String username = userMapper.getUserNameById(userId);
        String key = "bucket_" + username;
        int result = bucketMapper.update(userId, goodid, specid, number);
        return deleteRedisInfo(userId, key, result);
    }

    public int updateBucketNumber(int bucket, int number, int userId) {
        String username = userMapper.getUserNameById(userId);
        String key = "bucket_" + username;
        int result = bucketMapper.updateNumber(bucket, number);
        return deleteRedisInfo(userId, key, result);
    }

    public int updateBucketSpec(int userId, String goodid, String oldSpecid, String newSpecid) {
        String username = userMapper.getUserNameById(userId);
        String key = "bucket_" + username;
        BucketEntity oldBucket = bucketMapper.selectByGoodIdAndSpecificationId(userId, goodid, oldSpecid);
        BucketEntity newBucket = bucketMapper.selectByGoodIdAndSpecificationId(userId, goodid, newSpecid);
        int result = 1;
        if (newBucket != null) {
            newBucket.setNumber(newBucket.getNumber() + oldBucket.getNumber());
            bucketMapper.update(newBucket.getUserid(), newBucket.getGoodid(), newBucket.getSpecificationid(), newBucket.getNumber());
            result = bucketMapper.delete(oldBucket);
        } else {
            BucketEntity bucketEntity = new BucketEntity(userId, goodid, oldSpecid, oldBucket.getNumber());
            result = bucketMapper.updateOldSpecificationId(bucketEntity.getUserid(), bucketEntity.getGoodid(), bucketEntity.getSpecificationid(), bucketEntity.getNumber(), newSpecid);
        }
        return deleteRedisInfo(userId, key, result);
    }

    public int deleteBucket(int userId, String goodid, String specid) {
        String username = userMapper.getUserNameById(userId);
        String key = "bucket_" + username;
        BucketEntity bucketEntity = new BucketEntity(userId, goodid, specid);
        int result = bucketMapper.delete(bucketEntity);
        return deleteRedisInfo(userId, key, result);
    }

    public int deleteBucketByBucketId(int bucketId) {
        int userId = getUserId(bucketId);
        int result = bucketMapper.deleteByBucketId(bucketId);
        return deleteRedisInfo(userId, "bucket_" + userMapper.getUserNameById(userId), result);
    }

    private static Map<String, Object> getData(BucketEntity bucketEntity, GoodEntity goodEntity, SpecificationEntity specificationEntity) {
        Map<String, Object> goodData = new HashMap<>();
        goodData.put("bucketId", bucketEntity.getBucketid());
        goodData.put("goodId", goodEntity.getGoodID());
        goodData.put("goodName", goodEntity.getGoodName());
        goodData.put("specId", specificationEntity.getSpecificationID());
        goodData.put("specName", specificationEntity.getSpecName());
        goodData.put("specNumber", bucketEntity.getNumber());
        goodData.put("specPrice", specificationEntity.getPrice());
        goodData.put("specImage", specificationEntity.getShowurl());
        goodData.put("status", specificationEntity.isStatus());
        goodData.put("goodSelected", false);
        goodData.put("showDetails", false);
        return goodData;
    }

    private int deleteRedisInfo(int userId, String key, int result) {
        if (result == 1) {
            ValueOperations<String, Map<String, List<Map<String, Object>>>> valueOperations = redisTemplate.opsForValue();
            //判断redis中是否有键为key的缓存
            boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                valueOperations.getOperations().delete(key);
                logger.info("删除缓存:{}", key);
                getBucket(userId);
            }
        }
        return result;
    }

}
