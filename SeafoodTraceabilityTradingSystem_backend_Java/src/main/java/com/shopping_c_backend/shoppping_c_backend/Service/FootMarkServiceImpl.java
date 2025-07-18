package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.FootMarkEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.FootMarkMapper;
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
public class FootMarkServiceImpl {
    @Resource
    private FootMarkMapper footMarkMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private GoodServiceImpl goodService;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(FootMarkServiceImpl.class);

    public Map<String, List<GoodEntity>> getAllFootMarks(int userId) {
        List<FootMarkEntity> footMarks = footMarkMapper.findAllFootMark(userId);
        String username = userMapper.getUserNameById(userId);
        String key = "footMark_" + username;
        ValueOperations<String, Map<String, List<GoodEntity>>> valueOperations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            Map<String, List<GoodEntity>> cachedData = valueOperations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return cachedData;
        } else {
            Map<String, List<GoodEntity>> groupedData = new HashMap<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            for (FootMarkEntity entity : footMarks) {
                String date = dateFormat.format(entity.getTime());
                groupedData.putIfAbsent(date, new ArrayList<>());
                GoodEntity goodEntity = goodService.getGoodById(entity.getGoodId());
                groupedData.get(date).add(goodEntity);
            }
            List<String> sortedDates = new ArrayList<>(groupedData.keySet());
            sortedDates.sort(Collections.reverseOrder()); // 降序排序
            Map<String, List<GoodEntity>> sortedGroupedData = new LinkedHashMap<>();
            for (String date : sortedDates) {
                sortedGroupedData.put(date, groupedData.get(date));
            }
            logger.info("查询数据库获取数据:{}", key);
            logger.info("------------写入缓存-");
            //写入缓存
            valueOperations.set(key, sortedGroupedData, 5, TimeUnit.HOURS);
            return sortedGroupedData;
        }
    }

    public List<GoodEntity> getFootMarksByCount(int userId) {
        String username = userMapper.getUserNameById(userId);
        String key = "footMark_mostLike_" + username;
        ValueOperations<String, List<GoodEntity>> valueOperations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<GoodEntity> cachedData = valueOperations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return cachedData;
        } else {
            List<FootMarkEntity> footMarks = footMarkMapper.findFootMarkByUserCount(userId);
            if (footMarks.isEmpty()) return new ArrayList<>();
            logger.info("查询数据库获取数据:{}", key);
            logger.info("------------写入缓存-");
            //写入缓存
            List<GoodEntity> mostLike = new ArrayList<>();
            for (FootMarkEntity entity : footMarks) {
                GoodEntity goodEntity = goodService.getGoodById(entity.getGoodId());
                mostLike.add(goodEntity);
            }
            valueOperations.set(key, mostLike, 5, TimeUnit.HOURS);
            return mostLike;
        }
    }

    public int insertFootMark(int userId, String goodId) {
        Integer result = footMarkMapper.findFootMarkId(userId, goodId);
        boolean isLooked = result != null;
        int res;
        String username = userMapper.getUserNameById(userId);
        String key = "footMark_" + username;
        ValueOperations<String, Map<String, List<GoodEntity>>> valueOperations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            valueOperations.getOperations().delete(key);
            logger.info("删除缓存:{}", key);
        }
        if (isLooked) {
            res = footMarkMapper.updateFootMark(userId, goodId);
            getAllFootMarks(userId);
        } else {
            res = footMarkMapper.insertFootMark(userId, goodId);
            getAllFootMarks(userId);
        }
        deleteFootMarksByCount(userId);
        getFootMarksByCount(userId);
        if (res == 1) return 1;
        else return 0;
    }

    public int deleteFootMark(int userId, String goodId) {
        int result = footMarkMapper.deleteFootMark(userId, goodId);
        if (result == 1) {
            String username = userMapper.getUserNameById(userId);
            String key = "footMark_" + username;
            ValueOperations<String, Map<String, List<GoodEntity>>> valueOperations = redisTemplate.opsForValue();
            //判断redis中是否有键为key的缓存
            boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                valueOperations.getOperations().delete(key);
                logger.info("删除缓存:{}", key);
                getAllFootMarks(userId);
                deleteFootMarksByCount(userId);
                getFootMarksByCount(userId);
            }
            return 1;
        } else
            return 0;
    }

    public void deleteFootMarksByCount(int userId) {
        String username = userMapper.getUserNameById(userId);
        String key = "footMark_mostLike_" + username;
        ValueOperations<String, List<GoodEntity>> valueOperations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            valueOperations.getOperations().delete(key);
            logger.info("删除缓存:{}", key);
        }
    }
}
