package com.shopping_c_backend.shoppping_c_backend.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ShopUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceGoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceInformationEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.Trace.TraceUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.GoodMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.ShopMapper;
import com.shopping_c_backend.shoppping_c_backend.Mapper.TraceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TraceServiceImpl {
    @Resource
    private TraceMapper traceMapper;
    @Resource
    private GoodMapper goodMapper;
    @Resource
    private ShopMapper shopMapper;
    @Resource
    private RedisTemplate redisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(TraceServiceImpl.class);

    public List<Map<String, Object>> getShopTrace(int shopID) {
        String key = "ShopTraceList_" + shopID;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            List<Map<String, Object>> list = valueOperations.get(key);
            logger.info("从缓存中获取数据:{}", key);
            return list;
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            List<TraceEntity> traceEntities = traceMapper.selectByShopID(shopID);
            getTraceInfo(key, list, traceEntities);
            valueOperations.set(key, list, 5, TimeUnit.HOURS);
            return list;
        }
    }

    public List<Map<String, Object>> getGoodTrace(String goodID) {
        String key = "GoodTraceList_" + goodID;
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        boolean hasKey = redisTemplate.hasKey(key);
        List<Map<String, Object>> list;
        if (hasKey) {
            list = valueOperations.get(key);
            logger.info("从缓存中获取数据:{}", key);
        } else {
            list = new ArrayList<>();
            List<TraceGoodEntity> traceGoodEntities = traceMapper.selectGoodByTraceID(goodID);
            List<TraceEntity> traceEntities = new ArrayList<>();
            for (TraceGoodEntity traceGoodEntity : traceGoodEntities)
                traceEntities.add(traceMapper.selectTraceByTraceID(traceGoodEntity.getTraceID()));
            getTraceInfo(key, list, traceEntities);
        }
        return list;
    }

    private void getTraceInfo(String key, List<Map<String, Object>> list, List<TraceEntity> traceEntities) {
        for (TraceEntity traceEntity : traceEntities) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", traceEntity.getTraceID());
            StringBuilder title = new StringBuilder();
            List<TraceGoodEntity> traceGoodEntities1 = traceMapper.selectGoodByTraceID(traceEntity.getTraceID());
            int index = 0;
            for (TraceGoodEntity traceGoodEntity : traceGoodEntities1) {
                GoodEntity goodEntity = goodMapper.getGoodById(traceGoodEntity.getGoodID());
                title.append(goodEntity.getGoodName());
                if (index < traceGoodEntities1.size() - 1)
                    title.append(",");
                index++;
            }
            map.put("title", title.toString());
            if (title.length() > 15)
                map.put("firstTitle", title.substring(0, 15) + "...");
            else
                map.put("firstTitle", title.toString());
            map.put("date", traceEntity.getUpdateTime());
            List<Map<String, Object>> InfoList = new ArrayList<>();
            List<TraceInformationEntity> traceInformationEntities = traceMapper.selectTraceInformationByTraceID(traceEntity.getTraceID());
            for (TraceInformationEntity traceInformationEntity : traceInformationEntities) {
                Map<String, Object> infoMap = new HashMap<>();
                infoMap.put("timestamp", traceInformationEntity.getInsertTime());
                TraceUserEntity traceUserEntity = traceMapper.selectTraceUserByInformationID(traceInformationEntity.getLinkID());
                ShopUserEntity shopUserEntity = shopMapper.findUserById(traceUserEntity.getUserID());
                infoMap.put("operator", shopUserEntity.getNickname());
                infoMap.put("content", traceInformationEntity.getText());
                infoMap.put("images", traceInformationEntity.getImgUrL());
                InfoList.add(infoMap);
            }
            map.put("entries", InfoList);
            list.add(map);
        }
        logger.info("从数据库中获取数据:{}", key);
    }

    public List<String> createTrace(List<String> goodIDs, String text, int userID, int shopID) {
        String traceID = UUID.randomUUID().toString().substring(0, 12);
        int result;
        TraceEntity traceEntity = createTraceEntity(traceID, shopID);
        result = traceMapper.createTrace(traceEntity);
        if (result == 0) return null;
        String infoID = traceID + "-1";
        TraceInformationEntity traceInformationEntity = createTraceInformationEntity(infoID, traceID, text);
        result = traceMapper.createTraceInfo(traceInformationEntity);
        if (result == 0) return null;
        for (int i = 0; i < goodIDs.size(); i++) {
            String goodLinkID = traceID + "-" + (i + 1);
            String goodID = goodIDs.get(i);
            TraceGoodEntity traceGoodEntity = createTraceGoodEntity(goodLinkID, traceID, goodID);
            result = traceMapper.createTraceGood(traceGoodEntity);
            if (result == 0) return null;
        }
        String userLinkID = traceID + "-1";
        String type = "create";
        TraceUserEntity traceUserEntity = createTraceUserLinkEntity(userLinkID, traceID, userID, infoID, type);
        result = traceMapper.createTraceUser(traceUserEntity);
        if (result == 0) return null;
        List<String> traceList = new ArrayList<String>();
        traceList.add(traceID);
        traceList.add(infoID);
        String key = "ShopTraceList_" + shopID;
        deleteTraceRedisCache(key);
        getShopTrace(shopID);
        for (String goodID : goodIDs) {
            String goodKey = "GoodTraceList_" + goodID;
            deleteTraceRedisCache(goodKey);
            getGoodTrace(goodID);
        }
        return traceList;
    }

    public String addTrace(String traceID, String text, int userID, int shopID) {
        String newLinkID;
        TraceInformationEntity traceInformationEntity = traceMapper.selectLastTraceInformation(traceID);
        // 获取最后的-后的数字
        String[] split = traceInformationEntity.getLinkID().split("-");
        int index = Integer.parseInt(split[split.length - 1]);
        newLinkID = traceID + "-" + (index + 1);
        TraceInformationEntity newTraceInformationEntity = createTraceInformationEntity(newLinkID, traceID, text);
        int result = traceMapper.createTraceInfo(newTraceInformationEntity);
        if (result == 0) return null;
        TraceUserEntity traceUserEntity = traceMapper.selectLastTraceUser(traceID);
        // 获取最后的-后的数字
        String[] split2 = traceUserEntity.getLinkID().split("-");
        int index2 = Integer.parseInt(split2[split2.length - 1]);
        String newUserLinkID = traceID + "-" + (index2 + 1);
        String type = "add";
        TraceUserEntity newTraceUserEntity = createTraceUserLinkEntity(newUserLinkID, traceID, userID, newLinkID, type);
        result = traceMapper.createTraceUser(newTraceUserEntity);
        if (result == 0) return null;
        List<String> goodIDs = traceMapper.selectGoodIDByTraceID(traceID);
        String key = "ShopTraceList_" + shopID;
        deleteTraceRedisCache(key);
        getShopTrace(shopID);
        for (String goodID : goodIDs) {
            String goodKey = "GoodTraceList_" + goodID;
            deleteTraceRedisCache(goodKey);
            getGoodTrace(goodID);
        }
        return newLinkID;
    }

    public int updateTraceImg(String traceInfoID, String images) {
        JSONArray jsonArray = JSON.parseArray(images);
        TraceInformationEntity traceInformationEntity = traceMapper.selectInformationByLinkID(traceInfoID);
        traceInformationEntity.setImgUrL(jsonArray);
        int result = traceMapper.updateTraceImage(traceInformationEntity);
        if (result != 0) {
            TraceEntity traceEntity = traceMapper.selectTraceByTraceID(traceInformationEntity.getTraceID());
            List<String> goodIDs = traceMapper.selectGoodIDByTraceID(traceInformationEntity.getTraceID());
            String key = "ShopTraceList_" + traceEntity.getShopID();
            deleteTraceRedisCache(key);
            getShopTrace(traceEntity.getShopID());
            for (String goodID : goodIDs) {
                String goodKey = "GoodTraceList_" + goodID;
                deleteTraceRedisCache(goodKey);
                getGoodTrace(goodID);
            }
        }
        return result;
    }

    private TraceEntity createTraceEntity(String traceID, int shopID) {
        TraceEntity traceEntity = new TraceEntity();
        traceEntity.setTraceID(traceID);
        traceEntity.setShopID(shopID);
        traceEntity.setInsertTime(new Date());
        traceEntity.setUpdateTime(new Date());
        return traceEntity;
    }

    private TraceInformationEntity createTraceInformationEntity(String linkID, String traceID, String info) {
        TraceInformationEntity traceInformationEntity = new TraceInformationEntity();
        traceInformationEntity.setLinkID(linkID);
        traceInformationEntity.setTraceID(traceID);
        traceInformationEntity.setText(info);
        traceInformationEntity.setInsertTime(new Date());
        return traceInformationEntity;
    }

    private TraceGoodEntity createTraceGoodEntity(String linkID, String traceID, String goodID) {
        TraceGoodEntity traceGoodEntity = new TraceGoodEntity();
        traceGoodEntity.setLinkID(linkID);
        traceGoodEntity.setTraceID(traceID);
        traceGoodEntity.setGoodID(goodID);
        return traceGoodEntity;
    }

    private TraceUserEntity createTraceUserLinkEntity(String userLinkID, String traceID, int userID, String infoID, String type) {
        TraceUserEntity traceUserEntity = new TraceUserEntity();
        traceUserEntity.setLinkID(userLinkID);
        traceUserEntity.setTraceID(traceID);
        traceUserEntity.setUserID(userID);
        traceUserEntity.setInformationID(infoID);
        traceUserEntity.setType(type);
        traceUserEntity.setInsertTime(new Date());
        return traceUserEntity;
    }

    private void deleteTraceRedisCache(String key) {
        logger.info("删除缓存 {}", key);
        ValueOperations<String, List<Map<String, Object>>> valueOperations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey) {
            valueOperations.getOperations().delete(key);
            logger.info("删除已缓存商品信息缓存:{}", key);
        }
    }
}
