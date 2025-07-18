package com.shopping_c_backend.shoppping_c_backend.Websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    // 存储模块的会话：module -> (id -> (sessionId -> session))
    private static final Map<String, Map<String, Map<String, Session>>> moduleSessions = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);

    // 添加会话
    public static void addSession(String module, String id, Session session) {
        moduleSessions.putIfAbsent(module, new ConcurrentHashMap<>());
        moduleSessions.get(module).putIfAbsent(id, new ConcurrentHashMap<>());
        moduleSessions.get(module).get(id).put(session.getId(), session);
    }

    // 移除会话
    public static void removeSession(String module, String id, String sessionId) {
        Map<String, Map<String, Session>> moduleMap = moduleSessions.get(module);
        if (moduleMap != null) {
            Map<String, Session> idMap = moduleMap.get(id);
            if (idMap != null) {
                idMap.remove(sessionId);
                if (idMap.isEmpty()) {
                    moduleMap.remove(id);
                }
            }
            if (moduleMap.isEmpty()) {
                moduleSessions.remove(module);
            }
        }
    }

    // 获取指定模块和业务 ID 的所有会话
    public static Map<String, Session> getSessions(String module, String id) {
        Map<String, Map<String, Session>> moduleMap = moduleSessions.get(module);
        return moduleMap != null ? moduleMap.getOrDefault(id, null) : null;
    }

    // 向指定模块和业务 ID 的所有会话发送消息
    public static void sendMessage(String module, String id, String message) {
        Map<String, Session> sessions = getSessions(module, id);
        if (sessions != null) {
            sessions.forEach((sessionId, session) -> {
                try {
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    logger.error("Failed to send message to session: {}", session.getId(), e);
                }
            });
        }
    }
}
