package com.shopping_c_backend.module.goods;

import com.shopping_c_backend.common.cache.CacheService;
import com.shopping_c_backend.common.sanitizer.HtmlSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Refactored: Uses CacheService.getOrSet()????????.
 * Uses shared HtmlSanitizer instead of own PolicyFactory.
 */
@Service
public class GoodServiceImpl implements GoodService {

    private static final long CACHE_HOURS = 5;

    @Resource
    private GoodMapper goodMapper;
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private CacheService cacheService;
    @Resource
    private HtmlSanitizer htmlSanitizer;

    private static final Logger log = LoggerFactory.getLogger(GoodServiceImpl.class);

    @Override
    public List<GoodEntity> getGoods(int page, int size) {
        int offset = (page - 1) * size;
        return goodMapper.getGoods(offset, size);
    }

    @Override
    public List<GoodEntity> getGoodsRandom(int page, int size, List<String> recommendedItemId) {
        int offset = (page - 1) * size;
        return goodMapper.getGoodsRandom(offset, size);
    }

    @Override
    public List<GoodEntity> getGoodsByName(int page, int size, String name) {
        int offset = (page - 1) * size;
        return goodMapper.getGoodsByName(name, offset, size);
    }

    @Override
    public List<GoodEntity> getGoodsByShopId(int shopId) {
        return goodMapper.getGoodsByShopId(shopId);
    }

    @Override
    public int countGoods() {
        return goodMapper.getCount();
    }

    @Override
    public int countGoodsByName(String name) {
        return goodMapper.getCountByName(name);
    }

    @Override
    public Map<String, Object> getGoodAllInfo(String id) {
        String key = "good:allInfo:" + id;
        return cacheService.getOrSet(key, Map.class, () -> {
            int shopID = goodMapper.getShopID(id);
            var shopEntity = shopMapper.findShopById(shopID);
            var specificationEntities = goodMapper.getSpecificationByMainProductID(id);
            var goodEntity = goodMapper.getGoodById(id);

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("shopID", shopEntity.getShopID());
            resultMap.put("shopName", shopEntity.getShopName());
            resultMap.put("shopAvatar", shopEntity.getShopAvatar());
            resultMap.put("shopDesc", shopEntity.getShopDesc());
            resultMap.put("goodName", goodEntity.getGoodName());
            resultMap.put("goodUploadTime", goodEntity.getUplodatime());
            resultMap.put("goodLocation", goodEntity.getLocation());
            resultMap.put("specificationEntities", specificationEntities);
            return resultMap;
        }, CACHE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public List<SpecificationEntity> getSpecList(String id) {
        String key = "good:specList:" + id;
        return cacheService.getOrSet(key, List.class,
                () -> goodMapper.getSpecificationByMainProductID(id),
                CACHE_HOURS, TimeUnit.HOURS);
    }

    @Override
    public String sanitize(String input) {
        return htmlSanitizer.sanitize(input);
    }

    @Override
    public GoodEntity getGoodById(String id) {
        return goodMapper.getGoodById(id);
    }

    @Override
    public List<SpecificationEntity> getSpecificationByMainProductID(String id) {
        return goodMapper.getSpecificationByMainProductID(id);
    }
}
