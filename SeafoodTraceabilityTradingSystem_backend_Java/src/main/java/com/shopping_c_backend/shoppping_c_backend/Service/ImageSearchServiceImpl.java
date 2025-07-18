package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.GoodMapper;
import com.shopping_c_backend.shoppping_c_backend.Vo.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ImageSearchServiceImpl {
    @Value("${image.search.api.base-url}")
    private String apiBaseUrl;

    @Resource
    private GoodMapper goodMapper;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Logger logger = LoggerFactory.getLogger(ImageSearchServiceImpl.class);
    @Resource
    private RedisTemplate redisTemplate;

    public SearchResponse searchImage(MultipartFile imageFile) {
        try {
            // 临时将 MultipartFile 保存为 File
            File tempFile = File.createTempFile("upload", imageFile.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(imageFile.getBytes());
            }

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 构建请求体
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(tempFile));

            // 封装请求
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 调用 Django API
            String apiUrl = apiBaseUrl + "search_image/";
            ResponseEntity<SearchResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    SearchResponse.class
            );

            // 删除临时文件
            tempFile.delete();

            // 返回结果
            return response.getBody();

        } catch (Exception e) {
            logger.error("Error occurred while searching image", e);
            return null;
        }
    }

    public void storeSearchData(SearchResponse searchResponse) {
        String key = "search_data_" + searchResponse.getToken();
        ValueOperations<String, List<Map<String, Object>>> operations = redisTemplate.opsForValue();
        List<SearchResponse.Result> data = searchResponse.getData();
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (SearchResponse.Result result : data) {
            GoodEntity goodEntity = goodMapper.getGoodByImageId(result.getMilvus_id());
            Map<String, Object> map = new HashMap<>();
            map.put("goodId", goodEntity.getGoodID());
            map.put("goodName", goodEntity.getGoodName());
            map.put("price", goodEntity.getPrice());
            map.put("showUrl", goodEntity.getShowURL());
            map.put("comments", goodEntity.getComments());
            map.put("location", goodEntity.getLocation());
            resultList.add(map);
        }
        operations.set(key, resultList, 5, TimeUnit.MINUTES);
        logger.info("Search data stored in redis with key: {}", key);
    }

    public List<Map<String, Object>> getSearchData(String token, int offset, int size) {
        String key = "search_data_" + token;
        ValueOperations<String, List<Map<String, Object>>> operations = redisTemplate.opsForValue();
        List<Map<String, Object>> resultList = operations.get(key);
        if (resultList == null) {
            return new ArrayList<>();
        }
        int start = offset * size;
        int end = start + size;
        if (end > resultList.size()) {
            end = resultList.size();
        }
        return resultList.subList(start, end);
    }

}
