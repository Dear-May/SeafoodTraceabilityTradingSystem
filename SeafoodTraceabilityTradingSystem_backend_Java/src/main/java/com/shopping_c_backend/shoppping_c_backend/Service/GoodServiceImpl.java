package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.SpecificationEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.GoodMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ShopMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class GoodServiceImpl {
    @Resource
    private GoodMapper goodMapper;
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GoodServiceImpl.class);

    /**
     * 分页获取商品数据
     *
     * @param page 当前页码
     * @param size 每页显示的数量
     * @return 商品列表
     */
    public List<GoodEntity> getGoods(int page, int size) {
        int offset = (page - 1) * size; // 计算偏移量
        return goodMapper.getGoods(offset, size);
    }

    public List<GoodEntity> getGoodsRandom(int page, int size, List<String> recommendedItemId) {
        int offset = (page - 1) * size; // 计算偏移量
        return goodMapper.getGoodsRandom(offset, size);
    }

    public List<GoodEntity> getGoodsByName(int page, int size, String name) {
        int offset = (page - 1) * size; // 计算偏移量
        return goodMapper.getGoodsByName(name, offset, size);
    }

    public List<GoodEntity> getGoodsByShopId(int shopId) {
        return goodMapper.getGoodsByShopId(shopId);
    }

    /**
     * 获取商品总数
     *
     * @return 商品总数
     */
    public int countGoods() {
        return goodMapper.getCount();
    }

    public int countGoodsByName(String name) {
        return goodMapper.getCountByName(name);
    }

    public Map<String, Object> getGoodAllInfo(String id) {
        String key = "goodAllInfo_" + id;
        ValueOperations<String, Map<String, Object>> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            Map<String, Object> resultMap = operations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return resultMap;
        } else {
            int shopID = goodMapper.getShopID(id);
            ShopEntity shopEntity = shopMapper.findShopById(shopID);
            List<SpecificationEntity> specificationEntities = goodMapper.getSpecificationByMainProductID(id);
            GoodEntity goodEntity = goodMapper.getGoodById(id);
            Map<String, Object> resultMap = new HashMap<>();
            // 商家信息
            resultMap.put("shopID", shopEntity.getShopID());
            resultMap.put("shopName", shopEntity.getShopName());
            resultMap.put("shopAvatar", shopEntity.getShopAvatar());
            resultMap.put("shopDesc", shopEntity.getShopDesc());
            // 主商品信息
            resultMap.put("goodName", goodEntity.getGoodName());
            resultMap.put("goodUploadTime", goodEntity.getUplodatime());
            resultMap.put("goodLocation", goodEntity.getLocation());
            // 规格信息
            resultMap.put("specificationEntities", specificationEntities);
            logger.info("查询数据库获取数据:{}", key);
            logger.info("------------写入缓存-");
            //写入缓存
            operations.set(key, resultMap, 5, TimeUnit.HOURS);
            return resultMap;
        }
    }

    public List<SpecificationEntity> getSpecList(String id) {
        String key = "specList_" + id;
        ValueOperations<String, List<SpecificationEntity>> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<SpecificationEntity> resultList = operations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return resultList;
        } else {
            List<SpecificationEntity> specificationEntities = goodMapper.getSpecificationByMainProductID(id);
            logger.info("查询数据库获取数据:{}", key);
            logger.info("------------写入缓存-");
            //写入缓存
            operations.set(key, specificationEntities, 5, TimeUnit.HOURS);
            return specificationEntities;
        }
    }

    public GoodEntity getGoodById(String id) {
        return goodMapper.getGoodById(id);
    }

}
