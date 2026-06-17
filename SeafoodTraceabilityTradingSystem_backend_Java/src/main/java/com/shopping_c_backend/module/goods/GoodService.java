package com.shopping_c_backend.module.goods;

import java.util.List;
import java.util.Map;

public interface GoodService {
    List<GoodEntity> getGoods(int page, int size);
    List<GoodEntity> getGoodsRandom(int page, int size, List<String> recommendedItemId);
    List<GoodEntity> getGoodsByName(int page, int size, String name);
    List<GoodEntity> getGoodsByShopId(int shopId);
    int countGoods();
    int countGoodsByName(String name);
    Map<String, Object> getGoodAllInfo(String id);
    List<SpecificationEntity> getSpecList(String id);
    String sanitize(String input);
    GoodEntity getGoodById(String id);
    List<SpecificationEntity> getSpecificationByMainProductID(String id);
}
