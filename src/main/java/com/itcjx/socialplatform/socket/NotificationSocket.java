package com.itcjx.socialplatform.socket;

import com.itcjx.socialplatform.service.impl.UserServiceImpl;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/notification")
public class NotificationSocket implements ApplicationContextAware {
    //WebSocket实时推送（进阶版）
    private static final Logger log = LoggerFactory.getLogger(NotificationSocket.class);

    private static ApplicationContext context;
    private static JwtTokenUtil jwtTokenUtil;
    // 解决静态变量注入问题
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        jwtTokenUtil = context.getBean(JwtTokenUtil.class);
    }


    // 存储用户ID与Session的映射
    private static final Map<Long, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        // 用户连接时存储会话
        // 直接从 session 的查询参数中获取 token
        String token = session.getRequestParameterMap().get("token").get(0);
        // 清理 Bearer 和前后空格
        try {
            token = token.replace("Bearer ", "").trim();
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            userSessions.put(userId, session);
        } catch (Exception e) {
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "无效Token"));
            } catch (IOException ex) {
                log.error("关闭会话失败", ex);
            }
        }
        //token = token.replace("Bearer ", "").trim();
        //Long userId = jwtTokenUtil.getUserIdFromToken(token);
        //userSessions.put(userId, session);
        //System.out.println("用户连接: " + userId);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        userSessions.remove(userId);
        System.out.println("用户断开: " + userId);
    }

    @OnMessage
    public void onMessage(String message) {
        // 接收前端消息（如标记已读）
    }

    // 后端调用此方法推送新通知
    public static void sendNotification(Long userId, String message) {
        // 找到用户的Session并推送
        Session session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
