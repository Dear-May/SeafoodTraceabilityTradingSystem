package com.shopping_c_backend.shoppping_c_backend.Service;

import com.shopping_c_backend.shoppping_c_backend.Vo.EsGoodVo;

import java.io.IOException;
import java.util.List;

public interface EsSaveService {
    /**
     * 将商品数据保存到 Elasticsearch
     *
     * @param esVos 商品数据列表
     * @return 是否有失败
     * @throws IOException IO异常
     */
    boolean productStatusUp(List<EsGoodVo> esVos) throws IOException;
}

