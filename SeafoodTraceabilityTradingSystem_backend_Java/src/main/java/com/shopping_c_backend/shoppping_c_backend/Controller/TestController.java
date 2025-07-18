package com.shopping_c_backend.shoppping_c_backend.Controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import com.shopping_c_backend.shoppping_c_backend.Service.EsServiceImpl;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private EsServiceImpl service;

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
