package com.shopping_c_backend.Service;

import com.shopping_c_backend.module.goods.GoodEntity;
import com.shopping_c_backend.module.goods.SpecificationEntity;
import java.util.List;
import java.util.Map;

public interface GoodService {

    String sanitize(String input);
    List<GoodEntity> getGoods(int page, int size);
    List<GoodEntity> getGoodsRandom(int page, int size, List<String> recommendedItemId);
    List<GoodEntity> getGoodsByName(int page, int size, String name);
    List<GoodEntity> getGoodsByShopId(int shopId);
    int countGoods();
    int countGoodsByName(String name);
    Map<String, Object> getGoodAllInfo(String id);
    List<SpecificationEntity> getSpecList(String id);
    GoodEntity getGoodById(String id);
    String sanitize(String input);
}
