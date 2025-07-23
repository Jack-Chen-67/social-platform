package com.itcjx.socialplatform.controller;

import com.itcjx.socialplatform.entity.Notification;
import com.itcjx.socialplatform.service.impl.NotificationServiceImpl;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationServiceImpl notificationService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //查询未读通知列表
    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(@RequestHeader("Authorization") String token) {
        // 清理 Bearer 和前后空格
        token = token.replace("Bearer ", "").trim();
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return notificationService.getUnreadNotifications(userId);
    }

    //标记为已读
    @PostMapping("/{id}/read")
    public Result<Void> markAsRead(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        token = token.replace("Bearer ", "").trim();
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return notificationService.markAsRead(id, userId);
    }
}
