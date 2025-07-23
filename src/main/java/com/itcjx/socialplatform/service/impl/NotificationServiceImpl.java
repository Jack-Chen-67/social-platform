package com.itcjx.socialplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.socialplatform.entity.Notification;
import com.itcjx.socialplatform.mapper.NotificationMapper;
import com.itcjx.socialplatform.service.INotificationService;
import com.itcjx.socialplatform.socket.NotificationSocket;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    // 创建通知
    @Override
    public void createNotify(Long receiverId, Long senderId, String content, String type, Long articleId) {
        Notification notification = new Notification();
        notification.setReceiverId(receiverId);
        notification.setSenderId(senderId);
        notification.setContent(content);
        notification.setType(type);
        notification.setTargetId(articleId);
        notification.setCreatedAt(new Date());  // 补上创建时间
        notification.setIsRead(false);          // 默认未读

        this.save(notification);  // 关键！保存到数据库

        // 触发WebSocket推送
        NotificationSocket.sendNotification(
                receiverId,
                "{\"type\":\"NEW_NOTIFICATION\",\"content\":\"" + content + "\"}"
        );
    }

    // 获取未读通知
    public Result<List<Notification>> getUnreadNotifications(Long userId) {
        if(userId == null){return Result.error(Result.ErrorCode.PARAM_ERROR);}

        List<Notification> notifications = notificationMapper.selectList(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getReceiverId, userId)
                        .eq(Notification::getIsRead, 0)
                        .orderByDesc(Notification::getCreatedAt)
        );
        return Result.success(notifications);
    }

    // 标记为已读
    @Override
    public Result<Void> markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getReceiverId().equals(userId)) {
            return Result.error(Result.ErrorCode.DELETE_FAILED,"无权操作");
        }
        notification.setIsRead(true);
        notificationMapper.updateById(notification);
        return Result.success();
    }
}
