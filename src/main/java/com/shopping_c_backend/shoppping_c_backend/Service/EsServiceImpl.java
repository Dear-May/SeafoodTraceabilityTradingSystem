package com.shopping_c_backend.shoppping_c_backend.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.alibaba.fastjson.JSON;
import com.shopping_c_backend.shoppping_c_backend.Entity.GoodEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.SpecificationEntity;
import com.shopping_c_backend.shoppping_c_backend.Mapper.GoodMapper;
import com.shopping_c_backend.shoppping_c_backend.Vo.EsGoodVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service("EsSaveService")
public class EsServiceImpl implements EsSaveService {

    @Resource
    private ElasticsearchClient elasticsearchClient;
    @Resource
    private GoodMapper goodMapper;

    private static final Logger logger = LoggerFactory.getLogger(EsServiceImpl.class);

    @Override
    public boolean productStatusUp(List<EsGoodVo> esVos) throws IOException {
        // 创建 BulkRequest
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

        // 遍历传入的 EsGoodVo 数据
        for (EsGoodVo esVo : esVos) {
            // 将 EsGoodVo 转换为 JSON
            String jsonString = JSON.toJSONString(esVo);

            // 添加到 BulkRequest
            bulkRequestBuilder.operations(op -> op
                    .index(idx -> idx
                            .index("goods") // 替换为你的索引名称
                            .id(esVo.getGoodId()) // 使用商品ID作为文档ID
                            .document(jsonString)
                    )
            );
        }

        // 执行批量请求
        BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequestBuilder.build());

        // 检查是否有任何失败
        boolean hasFailures = bulkResponse.errors();

        // 打印失败的文档ID
        if (hasFailures) {
            bulkResponse.items().stream()
                    .filter(item -> item.error() != null)
                    .forEach(item -> System.err.println("Failed document ID: " + item.id() + ", error: " + item.error().reason()));
        }

        return hasFailures;
    }

    public int syncData() throws IOException {
        // 查询 MySQL 数据
        List<GoodEntity> goods = goodMapper.findAllGood();

        // 创建 BulkRequest
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

        for (GoodEntity good : goods) {
            EsGoodVo esVo = new EsGoodVo();
            esVo.setGoodId(good.getGoodID());
            esVo.setGoodName(good.getGoodName());
            esVo.setPrice(good.getPrice());
            esVo.setComments(good.getComments());
            esVo.setUploadTime(good.getUplodatime());
            esVo.setStatus(good.getStatus());
            esVo.setShowUrl(good.getShowURL());
            esVo.setShopId(good.getShopID());
            esVo.setLocation(good.getLocation());

            // 查询规格信息
            List<SpecificationEntity> specs = goodMapper.getValidSpecification(good.getGoodID());
            esVo.setSpecifications(specs != null ? specs.stream()
                    .map(spec -> new EsGoodVo.SpecificationVo(
                            spec.getSpecificationID(),
                            spec.getSpecName(),
                            spec.getPrice(),
                            spec.isStatus(),
                            spec.getUpdateTime(),
                            spec.getNumber(),
                            spec.getShowurl()))
                    .collect(Collectors.toList()) : Collections.emptyList());

            // 添加到 BulkRequest
            bulkRequestBuilder.operations(op -> op
                    .index(idx -> idx
                            .index("goods")
                            .id(esVo.getGoodId())
                            .document(esVo) // 直接传递对象
                    )
            );
        }

        // 执行批量同步到 Elasticsearch
        BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequestBuilder.build());

        if (bulkResponse.errors()) {
            bulkResponse.items().stream()
                    .filter(item -> item.error() != null)
                    .forEach(item -> logger.error("Failed document ID: {}, error: {}", item.id(), item.error().reason()));
        } else {
            logger.info("Successfully saved goods");
        }

        return goods.size();
    }

    /**
     * 商品搜索功能（不含价格筛选）
     *
     * @param keyword 搜索关键词（商品名称/描述）
     * @param page    页码
     * @param size    每页显示数量
     * @return 搜索结果
     * @throws IOException 如果发生错误
     */
    public List<EsGoodVo> searchGoods(String keyword, int page, int size) throws IOException {
        // 构建查询
        Query nameQuery = Query.of(q -> q
                .match(m -> m
                        .field("goodName") // 商品名称字段
                        .query(keyword) // 搜索关键词
                )
        );

        BoolQuery boolQuery = BoolQuery.of(b -> b
                .must(nameQuery) // 必须匹配商品名称
        );

        // 构建查询请求
        SearchRequest request = SearchRequest.of(r -> r
                .index("goods") // 替换为索引名称
                .query(q -> q.bool(boolQuery)) // 使用 Bool 查询
                .from((page - 1) * size) // 分页起始位置
                .size(size) // 每页数量
        );

        // 执行查询
        SearchResponse<EsGoodVo> response = elasticsearchClient.search(request, EsGoodVo.class);

        // 返回命中结果
        return response.hits().hits().stream()
                .map(Hit::source) // 获取 _source 数据
                .toList();
    }
}
