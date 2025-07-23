package com.itcjx.socialplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.socialplatform.entity.Notification;
import com.itcjx.socialplatform.util.Result;

import java.util.List;

public interface INotificationService extends IService<Notification> {
    //创建通知
    void createNotify(Long receiverId, Long senderId, String content, String type, Long articleId);
    // 获取未读通知
    Result<List<Notification>> getUnreadNotifications(Long userId);
    // 标记为已读
    Result<Void> markAsRead(Long notificationId, Long userId);
}
