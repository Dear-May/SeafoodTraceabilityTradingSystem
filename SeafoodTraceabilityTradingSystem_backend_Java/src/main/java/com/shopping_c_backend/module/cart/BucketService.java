package com.shopping_c_backend.module.cart;

import java.util.List;
import java.util.Map;

public interface BucketService {
    int getBucketNumber(int userId);
    List<Map<String, Object>> getBucket(int userId);
    int getUserId(int bucketId);
    int addBucket(int userId, String goodid, String specid, int number);
    int updateBucket(int userId, String goodid, String specid, int number);
    int updateBucketNumber(int bucket, int number, int userId);
    int updateBucketSpec(int userId, String goodid, String oldSpecid, String newSpecid);
    int deleteBucket(int userId, String goodid, String specid);
    int deleteBucketByBucketId(int bucketId);
}
