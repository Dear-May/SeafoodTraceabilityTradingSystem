package com.shopping_c_backend.shoppping_c_backend.Websocket.Handlers;

import com.shopping_c_backend.shoppping_c_backend.Websocket.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentMessageHandler implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentMessageHandler.class);

    @Override
    public void handle(String id, String message) {
        // 示例：处理支付相关的消息
        LOGGER.info("[payment] 处理支付消息：id={}，message={}", id, message);
        // 具体业务逻辑
    }
}
