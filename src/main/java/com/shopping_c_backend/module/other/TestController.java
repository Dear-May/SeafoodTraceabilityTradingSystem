package com.shopping_c_backend.module.other;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import com.shopping_c_backend.module.search.EsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RequestMapping("/test")
@RestController
public class TestController {
    @Resource
    private ElasticsearchClient client;
    @Resource
    private EsService service;

    @PostMapping("/es")
    public String testElasticsearch() {
        try {
            InfoResponse info = client.info();
            return "Cluster Name: " + info.clusterName();
        } catch (IOException e) {
            e.printStackTrace();
            return "Connection failed: " + e.getMessage();
        }
    }


}
