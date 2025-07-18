package com.itcjx.socialplatform.service.impl;

import com.itcjx.socialplatform.service.ICommentLikeService;
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
                    //取消点赞-->点赞
                    Long added = redisTemplate.opsForSet().add(key, userId);
                    return added > 0 ? Result.success("点赞成功") : Result.error(400, "点赞失败");
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
