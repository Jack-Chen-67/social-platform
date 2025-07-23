package com.itcjx.socialplatform.service.impl;

import com.itcjx.socialplatform.entity.Article;
import com.itcjx.socialplatform.entity.Comment;
import com.itcjx.socialplatform.mapper.ArticleMapper;
import com.itcjx.socialplatform.mapper.CommentMapper;
import com.itcjx.socialplatform.mapper.UserMapper;
import com.itcjx.socialplatform.service.ICommentLikeService;
import com.itcjx.socialplatform.socket.NotificationSocket;
import com.itcjx.socialplatform.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CommentLikeServiceImpl implements ICommentLikeService {

    private static final Logger log = LoggerFactory.getLogger(CommentLikeServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationServiceImpl notificationService;

    //评论点赞/取消点赞
    @Override
    public Result<String> Commentlike(Long commentId, Long userId) {
        if(userId == null || userId == 0L){
            return Result.error(401, "请先登录");
        }else if (commentId == null || commentId == 0L) {
            return Result.error(400, "请选择评论");
        }

        String key = "article:comment:like:" + commentId;
        int retryCount = 3;
        while (retryCount-- > 0) {
            try{
                boolean isMember = redisTemplate.opsForSet().isMember(key, userId);
                if(Boolean.TRUE.equals(isMember)){
                    //点赞-->取消点赞
                    Long removed = redisTemplate.opsForSet().remove(key, userId);
                    return removed > 0 ? Result.success("取消点赞成功") : Result.error(400, "取消点赞失败");
                }else{
                    //取消点赞->点赞
                    Long added = redisTemplate.opsForSet().add(key, userId);
                    if (added != null && added > 0) {

                        // 新增：触发通知（评论作者接收）
                        Comment comment = commentMapper.selectById(commentId);
                        if (!comment.getUserId().equals(userId)) {  // 自己不通知自己
                            notificationService.createNotify(
                                    comment.getUserId(),  // 接收者ID = 评论作者
                                    userId,               // 发送者ID = 点赞用户
                                    "你的评论《" + comment.getContent() + "》被用户" + userMapper.selectById(userId).getUsername() + "点赞",
                                    "LIKE",
                                    commentId
                            );
                            // socket
                            NotificationSocket.sendNotification(
                                    comment.getUserId(),
                                    "{\"type\":\"like\",\"content\":\"用户点赞了你的评论\"}"
                            );
                        }

                        return Result.success("点赞成功");
                    } else {
                        return Result.error(Result.ErrorCode.BUSINESS_ERROR, "操作失败，请重试");
                    }
                    //取消点赞-->点赞
                    //Long added = redisTemplate.opsForSet().add(key, userId);
                    //return added > 0 ? Result.success("点赞成功") : Result.error(400, "点赞失败");
                }
            }catch (Exception e){
                log.error("点赞操作异常，正在重试...", e); // 关键！打印完整堆栈
                if (retryCount == 0) {
                    return Result.error(Result.ErrorCode.SERVER_ERROR, "系统异常，请稍后再试");
                }
            }
        }
        return Result.error(Result.ErrorCode.SERVER_ERROR, "系统异常，请稍后再试");
    }

    //获取评论点赞数
    @Override
    public Result<Long> getCommentLikeCount(Long commentId) {
        if(commentId == null && commentId == 0){
            return Result.error(Result.ErrorCode.PARAM_ERROR);
        }
        String key = "article:comment:like:" + commentId;
        Long count = redisTemplate.opsForSet().size(key);
        return Result.success(count != null ? count : 0L);
    }
}
