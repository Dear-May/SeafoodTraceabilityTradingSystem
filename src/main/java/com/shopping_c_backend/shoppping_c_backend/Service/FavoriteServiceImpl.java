package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.FavoriteEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.FavoriteMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FavoriteServiceImpl {
    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private GoodServiceImpl goodService;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    public List<GoodEntity> getFavoriteList(int userId) {
        String name = userMapper.getUserNameById(userId);
        String key = "favorite_" + name;
        ValueOperations<String, List<GoodEntity>> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            //如果有缓存，则从缓存中获取数据
            logger.info("从缓存中获取数据:{}", key);
            List<GoodEntity> goodEntities = operations.get(key);
            return goodEntities;
        } else {
            List<String> goodIdList = favoriteMapper.getFavoriteInfoByUserId(userId);
            if (goodIdList != null) {
                List<GoodEntity> goodEntities = new ArrayList<>();
                for (String goodId : goodIdList) {
                    GoodEntity goodEntity = goodService.getGoodById(goodId);
                    goodEntities.add(goodEntity);
                }
                logger.info("从数据库获取数据:{}", key);
                logger.info("写入缓存:{}", key);
                //写入缓存
                operations.set(key, goodEntities, 5, TimeUnit.HOURS);
                return goodEntities;
            }
            return null;
        }
    }

    public boolean isFavorite(int userId, String goodId) {
        FavoriteEntity favoriteEntity = favoriteMapper.getFavoriteInfo(userId, goodId);
        return favoriteEntity != null;
    }

    public int insertFavorite(int userId, String goodId) {
        String name = userMapper.getUserNameById(userId);
        String key = "favorite_" + name;
        FavoriteEntity favoriteEntity = new FavoriteEntity(userId, goodId);
        int result = favoriteMapper.insertFavoriteInfo(favoriteEntity);
        return RedisFunction(userId, key, result);
    }

    public int deleteFavorite(int userId, String goodId) {
        String name = userMapper.getUserNameById(userId);
        String key = "favorite_" + name;
        int result = favoriteMapper.deleteFavoriteInfoByUserId(userId, goodId);
        return RedisFunction(userId, key, result);
    }

    private int RedisFunction(int userId, String key, int result) {
        if (result == 1) {
            ValueOperations<String, List<GoodEntity>> operations = redisTemplate.opsForValue();
            //判断redis中是否有键为key的缓存
            boolean hasKey = redisTemplate.hasKey(key);
            if (hasKey) {
                //如果有缓存，则从缓存中获取数据，并将新添加的商品添加到缓存中
                operations.getOperations().delete(key);
                logger.info("从缓存中删除数据:{}", key);
                getFavoriteList(userId);
                return 1;
            }
        }
        return result;
    }
}
