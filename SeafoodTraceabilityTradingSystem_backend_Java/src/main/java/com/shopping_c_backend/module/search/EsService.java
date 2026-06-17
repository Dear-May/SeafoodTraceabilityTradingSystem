package com.shopping_c_backend.module.search;

import com.shopping_c_backend.module.search.EsGoodVo;
import java.io.IOException;
import java.util.List;

public interface EsService {
    List<EsGoodVo> searchGoods(String keyword, int page, int size) throws IOException;
    int syncData() throws IOException;
}
