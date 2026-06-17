package com.shopping_c_backend.Service;

import com.shopping_c_backend.module.goods.GoodEntity;
import java.util.List;
import java.util.Map;

public interface FootMarkService {
    Map<String, List<GoodEntity>> getAllFootMarks(int userId);
    List<GoodEntity> getFootMarksByCount(int userId);
    int insertFootMark(int userId, String goodId);
    int deleteFootMark(int userId, String goodId);
    void deleteFootMarksByCount(int userId);
}
