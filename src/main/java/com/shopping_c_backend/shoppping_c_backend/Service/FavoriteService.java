package com.shopping_c_backend.Service;

import com.shopping_c_backend.module.goods.GoodEntity;
import java.util.List;

public interface FavoriteService {
    List<GoodEntity> getFavoriteList(int userId);
    boolean isFavorite(int userId, String goodId);
    int insertFavorite(int userId, String goodId);
    int deleteFavorite(int userId, String goodId);
}
