package com.shopping_c_backend.shoppping_c_backend.Config;

import com.shopping_c_backend.shoppping_c_backend.Websocket.WebSocketRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {

        ServerEndpointExporter exporter = new ServerEndpointExporter();

        // 手动注册 WebSocket 端点
        exporter.setAnnotatedEndpointClasses(WebSocketRouter.class);

        return exporter;
    }
}
