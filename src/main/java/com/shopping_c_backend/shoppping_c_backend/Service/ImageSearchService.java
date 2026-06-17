package com.shopping_c_backend.Service;

import com.shopping_c_backend.module.search.SearchResponse;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface ImageSearchService {
    SearchResponse searchImage(MultipartFile imageFile);
    void storeSearchData(SearchResponse searchResponse);
    List<Map<String, Object>> getSearchData(String token, int offset, int size);
}
