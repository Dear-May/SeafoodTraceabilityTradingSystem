package com.shopping_c_backend.module.shop;

import com.shopping_c_backend.module.goods.GoodEntity;
import com.shopping_c_backend.module.shop.ReviewLicenseEntity;
import com.shopping_c_backend.module.shop.ShopEntity;
import com.shopping_c_backend.common.web.Result;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface ShopService {
    ShopEntity findById(int id);
    boolean findFollowShop(int shopId, int userId);
    int followShop(int shopId, int userId);
    int unfollowShop(int shopId, int userId);
    ShopEntity createShop(ShopEntity shopEntity);
    int updateShopInfo(ShopEntity shopEntity);
    int addReviewLincese(ReviewLicenseEntity reviewLicenseEntity);
    ReviewLicenseEntity findReviewLicenseById(String id);
    int updateReviewLicense(ReviewLicenseEntity reviewLicenseEntity);
    boolean isAccessShop(int shopId);
    List<Map<String, Object>> getCardInfo(int shopId);
    List<Map<String, Object>> getProductInfo(int shopId, int page, int pageSize);
    List<Map<String, Object>> getOrderInfo(int shopId, int page, int pageSize, Map<String, Object> searchParams);
    Map<String, Object> getShopOrderInfo(int shopId);
    List<Map<String, Object>> getReturnOrderInfo(int shopId, int page, int pageSize);
    Map<String, Object> getShopReturnOrderInfo(int shopId);
    Result updateReturnOrderStatus(String returnId, String newStatus, String token);
    Map<String, Object> getMerchantChartOrderDetail(int userId, int shopId);
    int sendMessage(int userId, int shopId, String content, String type);
    Result createGoods(GoodEntity goodEntity, MultipartFile[] files, MultipartFile showFile, String specifications) throws Exception;
    Result updateGoods(GoodEntity goodEntity, MultipartFile[] files, MultipartFile showFile, String specifications, String deleteImages, String deleteSpecs) throws Exception;
}
