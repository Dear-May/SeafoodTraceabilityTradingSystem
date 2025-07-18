package com.shopping_c_backend.shoppping_c_backend.Websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/channel/{module}/{id}")
public class WebSocketRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketRouter.class);

    private String module;
    private String id;
    private Session session;

    @OnOpen
    public void onOpen(Session session, @PathParam("module") String module, @PathParam("id") String id) {
        this.session = session;
        this.module = module;
        this.id = id;

        // 注册会话
        SessionManager.addSession(module, id, session);
        LOGGER.info("[websocket] 新的连接：module={}，id={}，sessionId={}", module, id, session.getId());
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.info("[websocket] 收到消息：module={}，id={}，sessionId={}，message={}", module, id, session.getId(), message);

        // 获取对应模块的消息处理器
        MessageHandler handler = MessageHandlerFactory.getHandler(module);
        handler.handle(id, message);
    }

    @OnClose
    public void onClose(CloseReason closeReason) {
        // 移除会话
        SessionManager.removeSession(module, id, session.getId());
        LOGGER.info("[websocket] 连接断开：module={}，id={}，sessionId={}，reason={}", module, id, session.getId(), closeReason);
    }

    @OnError
    public void onError(Throwable throwable) {
        LOGGER.error("[websocket] 连接异常：module={}，id={}，sessionId={}，error={}", module, id, session.getId(), throwable.getMessage());
    }
}
