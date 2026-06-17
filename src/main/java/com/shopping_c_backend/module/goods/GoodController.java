package com.shopping_c_backend.module.goods;

import com.shopping_c_backend.module.goods.GoodEntity;
import com.shopping_c_backend.module.goods.SpecificationEntity;
import com.shopping_c_backend.module.user.UserBehaviorEntity;
import com.shopping_c_backend.Service.*;
import com.shopping_c_backend.module.search.EsGoodVo;
import com.shopping_c_backend.common.web.Result;
import com.shopping_c_backend.module.search.SearchResponse;
// OWASP sanitization moved to service layer
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/good")
public class GoodController {
    @Resource
    private GoodServiceImpl goodService;
    @Resource
    private FootMarkServiceImpl footMarkService;
    @Resource
    private FavoriteServiceImpl favoriteService;
    @Resource
    private BucketServiceImpl bucketService;
    @Resource
    private EsServiceImpl esService;
    @Resource
    private UserBehaviorService userBehaviorService;
    @Resource
    private ImageSearchServiceImpl imageSearchServiceImpl;
    // Sanitization now handled via GoodService.sanitize()

    @RequestMapping(value = "/getGoods", method = RequestMethod.POST, produces = "application/json")
    public List<GoodEntity> getGoods(@RequestBody Map<String, Object> requestMap) {
        Object userIdObj = requestMap.get("userId");
        int userId = 0; // 默认值为0
        if (userIdObj != null) {
            String userIdStr = userIdObj.toString().trim();
            // 只有当userIdStr既不为空且为有效数字时才进行转换
            if (!userIdStr.isEmpty()) {
                try {
                    userId = Integer.parseInt(goodService.sanitize(userIdStr));
                } catch (NumberFormatException e) {
                    // 如果格式不正确，也无需抛出异常，保持userId=0即可
                }
            }
        }
        int page = Integer.parseInt(goodService.sanitize(requestMap.get("page") == null ? "1" : requestMap.get("page").toString()));
        int pageSize = Integer.parseInt(goodService.sanitize(requestMap.get("pageSize") == null ? "10" : requestMap.get("pageSize").toString()));
        if (userId != 0) {
            List<UserBehaviorEntity> userBehaviorEntities = userBehaviorService.getUserBehaviors(userId);
            Map<Integer, Map<String, Double>> userItemMatrix = userBehaviorService.buildUserItemMatrix(userBehaviorEntities);
            Map<String, Map<String, Double>> itemSimilarityMatrix = userBehaviorService.calculateItemSimilarity(userItemMatrix);
            List<String> recommendedItemIds = userBehaviorService.recommendItems(userId, itemSimilarityMatrix, userItemMatrix, page, pageSize);
            return goodService.getGoodsRandom(page, pageSize, recommendedItemIds);
        }
        return goodService.getGoods(page, pageSize);
    }

    @RequestMapping(value = "/getGoodByName", method = RequestMethod.POST, produces = "application/json")
    public List<GoodEntity> getGoodByName(@RequestBody Map<String, Object> requestMap) {
        int page = Integer.parseInt(goodService.sanitize(requestMap.get("page") == null ? "1" : requestMap.get("page").toString()));
        int pageSize = Integer.parseInt(goodService.sanitize(requestMap.get("pageSize") == null ? "10" : requestMap.get("pageSize").toString()));
        String goodName = goodService.sanitize(requestMap.get("goodName") == null ? "" : requestMap.get("goodName").toString());
        return goodService.getGoodsByName(page, pageSize, goodName);
    }

    @RequestMapping(value = "/getGoodByShopId", method = RequestMethod.POST, produces = "application/json")
    public List<GoodEntity> getGoodByShopId(@RequestBody Map<String, Object> requestMap) {
        int shopId = Integer.parseInt(goodService.sanitize(requestMap.get("shopId") == null ? "1" : requestMap.get("shopId").toString()));
        return goodService.getGoodsByShopId(shopId);
    }

    @RequestMapping(value = "/getCount", method = RequestMethod.POST, produces = "application/json")
    public int getCount() {
        return goodService.countGoods();
    }

    @RequestMapping(value = "/getCountByName", method = RequestMethod.POST, produces = "application/json")
    public int getCountByName(@RequestBody Map<String, Object> requestMap) {
        String goodName = goodService.sanitize(requestMap.get("goodName") == null ? "" : requestMap.get("goodName").toString());
        return goodService.countGoodsByName(goodName);
    }

    @RequestMapping(value = "/getGoodAllInfo", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getGoodAllInfo(@RequestBody Map<String, Object> requestMap) {
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        Map<String, Object> map = goodService.getGoodAllInfo(goodId);
        return map;
    }

    // 历史记录部分
    @RequestMapping(value = "/addGoodFootMark", method = RequestMethod.POST, produces = "application/json")
    public Result addGoodFootMark(@RequestBody Map<String, Object> requestMap) {
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        return footMarkService.insertFootMark(userId, goodId) == 1 ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "/findGoodFootMark", method = RequestMethod.POST, produces = "application/json")
    public Map<String, List<GoodEntity>> findGoodFootMark(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        return footMarkService.getAllFootMarks(userId);
    }

    @RequestMapping(value = "/deleteGoodFootMark", method = RequestMethod.POST, produces = "application/json")
    public Result deleteGoodFootMark(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        return footMarkService.deleteFootMark(userId, goodId) == 1 ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "/getUserLikeInFootMark", method = RequestMethod.POST, produces = "application/json")
    public List<GoodEntity> getUserLikeInFootMark(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        return footMarkService.getFootMarksByCount(userId);
    }

    // 收藏部分
    @RequestMapping(value = "/isFavourite", method = RequestMethod.POST, produces = "application/json")
    public Result isFavourite(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        return favoriteService.isFavorite(userId, goodId) ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "/addFavourite", method = RequestMethod.POST, produces = "application/json")
    public Result addFavourite(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        return favoriteService.insertFavorite(userId, goodId) == 1 ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "/findFavourite", method = RequestMethod.POST, produces = "application/json")
    public List<GoodEntity> findFavourite(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        return favoriteService.getFavoriteList(userId);
    }

    @RequestMapping(value = "/deleteFavourite", method = RequestMethod.POST, produces = "application/json")
    public Result deleteFavourite(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        return favoriteService.deleteFavorite(userId, goodId) == 1 ? Result.success() : new Result(400);
    }

    // 购物车部分
    @RequestMapping(value = "/getCartCount", method = RequestMethod.POST, produces = "application/json")
    public int getCartCount(@RequestBody Map<String, Object> requestMap) {
        Object userIdObj = requestMap.get("userId");
        int userId = 0; // 默认值为0
        if (userIdObj != null) {
            String userIdStr = userIdObj.toString().trim();
            // 只有当userIdStr既不为空且为有效数字时才进行转换
            if (!userIdStr.isEmpty()) {
                try {
                    userId = Integer.parseInt(goodService.sanitize(userIdStr));
                } catch (NumberFormatException e) {
                    // 如果格式不正确，也无需抛出异常，保持userId=0即可
                }
            }
        }
        return bucketService.getBucketNumber(userId);
    }

    @RequestMapping(value = "/getCartList", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> findCartList(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        return bucketService.getBucket(userId);
    }

    @RequestMapping(value = "/addGoodCart", method = RequestMethod.POST, produces = "application/json")
    public Result addGoodCart(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        String specId = goodService.sanitize(requestMap.get("specId") == null ? "" : requestMap.get("specId").toString());
        int number = Integer.parseInt(goodService.sanitize(requestMap.get("number") == null ? "" : requestMap.get("number").toString()));
        return bucketService.addBucket(userId, goodId, specId, number) == 1 ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "/getSpecList", method = RequestMethod.POST, produces = "application/json")
    public List<SpecificationEntity> getSpecList(@RequestBody Map<String, Object> requestMap) {
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        return goodService.getSpecList(goodId);
    }

    @RequestMapping(value = "/updateGoodCart", method = RequestMethod.POST, produces = "application/json")
    public Result updateGoodCart(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        String specId = goodService.sanitize(requestMap.get("specId") == null ? "" : requestMap.get("specId").toString());
        int number = Integer.parseInt(goodService.sanitize(requestMap.get("number") == null ? "" : requestMap.get("number").toString()));
        return bucketService.updateBucket(userId, goodId, specId, number) == 1 ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "/updateGoodCartSpec", method = RequestMethod.POST, produces = "application/json")
    public Result updateGoodCartSpec(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        String oldSpecId = goodService.sanitize(requestMap.get("oldSpecId") == null ? "" : requestMap.get("oldSpecId").toString());
        String newSpecId = goodService.sanitize(requestMap.get("newSpecId") == null ? "" : requestMap.get("newSpecId").toString());
        if (oldSpecId.equals(newSpecId))
            return Result.success();
        return bucketService.updateBucketSpec(userId, goodId, oldSpecId, newSpecId) == 1 ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "/deleteGoodCart", method = RequestMethod.POST, produces = "application/json")
    public Result deleteGoodCart(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        String specId = goodService.sanitize(requestMap.get("specId") == null ? "" : requestMap.get("specId").toString());
        return bucketService.deleteBucket(userId, goodId, specId) == 1 ? Result.success() : new Result(400);
    }

    @RequestMapping(value = "moveToFavorite", method = RequestMethod.POST, produces = "application/json")
    public Result moveToFavorite(@RequestBody Map<String, Object> requestMap) {
        int userId = Integer.parseInt(goodService.sanitize(requestMap.get("userId") == null ? "" : requestMap.get("userId").toString()));
        String goodId = goodService.sanitize(requestMap.get("goodId") == null ? "" : requestMap.get("goodId").toString());
        String specId = goodService.sanitize(requestMap.get("specId") == null ? "" : requestMap.get("specId").toString());
        int result = bucketService.deleteBucket(userId, goodId, specId);
        if (result == 1) {
            if (!favoriteService.isFavorite(userId, goodId)) {
                return new Result(favoriteService.insertFavorite(userId, goodId));
            } else
                return Result.success();
        }
        return new Result(400);
    }

    // es部分
    @RequestMapping(value = "/searchGood", method = RequestMethod.POST, produces = "application/json")
    public List<EsGoodVo> searchGood(@RequestBody Map<String, Object> requestMap) throws IOException {
        String keyword = goodService.sanitize(requestMap.get("keyword") == null ? "" : requestMap.get("keyword").toString());
        int page = Integer.parseInt(goodService.sanitize(requestMap.get("page") == null ? "" : requestMap.get("page").toString()));
        int pageSize = Integer.parseInt(goodService.sanitize(requestMap.get("pageSize") == null ? "" : requestMap.get("pageSize").toString()));
        return esService.searchGoods(keyword, page, pageSize);
    }

    //以图识图部分
    @RequestMapping(value = "/image-search", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> searchImage(@RequestParam("image") MultipartFile imageFile) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            SearchResponse result = imageSearchServiceImpl.searchImage(imageFile);
            if (Objects.equals(result.getStatus(), "success")) {
                imageSearchServiceImpl.storeSearchData(result);
                resultMap.put("code", 200);
                resultMap.put("token", result.getToken());
            } else
                resultMap.put("code", 400);
        } catch (Exception e) {
            resultMap.put("code", 500);
        }
        return resultMap;
    }

    @RequestMapping(value = "/get-search-result", method = RequestMethod.POST, produces = "application/json")
    public List<Map<String, Object>> getSearchResult(@RequestBody Map<String, Object> requestMap) {
        String token = requestMap.get("token") == null ? "" : requestMap.get("token").toString();
        int page = requestMap.get("page") == null ? 1 : Integer.parseInt(requestMap.get("page").toString());
        int pageSize = requestMap.get("pageSize") == null ? 10 : Integer.parseInt(requestMap.get("pageSize").toString());
        return imageSearchServiceImpl.getSearchData(token, page, pageSize);
    }
}
