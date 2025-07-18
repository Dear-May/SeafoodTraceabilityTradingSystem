package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.*;
import com.shopping_c_backend.shoppping_c_backend.Mapper.BucketMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.FavoriteMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.FootMarkMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserBehaviorService {
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private BucketMapper bucketMapper;
    @Resource
    private FavoriteMapper favoriteMapper;
    @Resource
    private FootMarkMapper footMarkMapper;

    public List<UserBehaviorEntity> getUserBehaviors(int userid) {
        List<UserBehaviorEntity> behaviors = new ArrayList<>();
        List<OrderEntity> orders = orderMapper.getOrderInfoByUserid(userid);
        List<BucketEntity> buckets = bucketMapper.selectByUserId(userid);
        List<FavoriteEntity> favorites = favoriteMapper.getFavoriteInfoByUserIdEntity(userid);
        List<FootMarkEntity> footMarks = footMarkMapper.findAllFootMark(userid);
        for (OrderEntity order : orders) {
            List<OrderItemEntity> orderItems = orderMapper.getOrderItemByOrderId(order.getOrderid());
            for (OrderItemEntity orderItem : orderItems)
                behaviors.add(new UserBehaviorEntity(order.getUserid(), orderItem.getGood_id(), "order"));
        }
        for (BucketEntity bucket : buckets)
            behaviors.add(new UserBehaviorEntity(bucket.getUserid(), bucket.getGoodid(), "bucket"));
        for (FavoriteEntity favorite : favorites)
            behaviors.add(new UserBehaviorEntity(favorite.getUserid(), favorite.getGoodid(), "favorite"));
        for (FootMarkEntity footMark : footMarks)
            behaviors.add(new UserBehaviorEntity(footMark.getUserid(), footMark.getGoodId(), "footMark"));
        return behaviors;
    }

    // 协同过滤算法
    // 构建用户-商品评分矩阵
    public Map<Integer, Map<String, Double>> buildUserItemMatrix(List<UserBehaviorEntity> userBehaviors) {
        Map<Integer, Map<String, Double>> matrix = new HashMap<>();
        for (UserBehaviorEntity behavior : userBehaviors) {
            matrix.putIfAbsent(behavior.getUserId(), new HashMap<>());
            double score = switch (behavior.getBehavior()) {
                case "footMark" -> 1; // 浏览权重
                case "order" -> 5;  // 订单权重
                case "bucket" -> 3;  // 加入购物车权重
                case "favorite" -> 3;
                default -> 0; // 收藏权重
            };
            matrix.get(behavior.getUserId()).merge(behavior.getGoodId(), score, Double::sum);
        }
        return matrix;
    }

    // 计算商品相似度矩阵
    public Map<String, Map<String, Double>> calculateItemSimilarity(Map<Integer, Map<String, Double>> userItemMatrix) {
        Map<String, Map<String, Double>> similarityMatrix = new HashMap<>();
        Set<String> items = userItemMatrix.values().stream()
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toSet());

        for (String item1 : items) {
            for (String item2 : items) {
                if (!item1.equals(item2)) {
                    double similarity = calculateCosineSimilarity(item1, item2, userItemMatrix);
                    similarityMatrix.putIfAbsent(item1, new HashMap<>());
                    similarityMatrix.get(item1).put(item2, similarity);
                }
            }
        }
        return similarityMatrix;
    }

    // 计算余弦相似度
    private double calculateCosineSimilarity(String item1, String item2, Map<Integer, Map<String, Double>> userItemMatrix) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (Map<String, Double> userRatings : userItemMatrix.values()) {
            double rating1 = userRatings.getOrDefault(item1, 0.0);
            double rating2 = userRatings.getOrDefault(item2, 0.0);
            dotProduct += rating1 * rating2;
            normA += Math.pow(rating1, 2);
            normB += Math.pow(rating2, 2);
        }
        return normA == 0 || normB == 0 ? 0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // 根据用户历史记录推荐商品
    public List<String> recommendItems(int userId, Map<String, Map<String, Double>> itemSimilarityMatrix,
                                       Map<Integer, Map<String, Double>> userItemMatrix, int page, int pageSize) {
        Map<String, Double> scores = new HashMap<>();
        Map<String, Double> userRatings = userItemMatrix.getOrDefault(userId, Collections.emptyMap());

        for (Map.Entry<String, Double> entry : userRatings.entrySet()) {
            String ratedItem = entry.getKey();
            Double rating = entry.getValue();

            Map<String, Double> similarItems = itemSimilarityMatrix.getOrDefault(ratedItem, Collections.emptyMap());
            for (Map.Entry<String, Double> similarItemEntry : similarItems.entrySet()) {
                String similarItem = similarItemEntry.getKey();
                Double similarity = similarItemEntry.getValue();

                if (!userRatings.containsKey(similarItem)) { // 未评分的商品
                    scores.merge(similarItem, similarity * rating, Double::sum);
                }
            }
        }

        // 排序并分页
        return scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // 按得分降序排序
                .skip((long) (page - 1) * pageSize) // 分页
                .limit(pageSize)
                .map(Map.Entry::getKey)
                .toList();
    }

}
