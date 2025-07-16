package com.itcjx.socialplatform.service.impl;

import com.itcjx.socialplatform.config.RedisConfiguration;
import com.itcjx.socialplatform.service.ILikeService;
import com.itcjx.socialplatform.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements ILikeService {
    private static final Logger log = LoggerFactory.getLogger(LikeServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞和取消点赞
    @Override
    public Result<String> like(Long articleId, Long userId) {
        if (articleId == null || articleId <= 0) {
            return Result.error(Result.ErrorCode.NOT_FOUND,"文章不存在");
        }
        if (userId == null || userId <= 0) {
            return Result.error(Result.ErrorCode.NOT_FOUND,"用户不存在，请登录或重新登录后重试");
        }
        //key
        String key = "article:like:" + articleId;
        int retryCount = 3;

        while (retryCount-- > 0) {
            try {
                //点赞
                boolean isMember = redisTemplate.opsForSet().isMember(key, userId);
                if(Boolean.TRUE.equals(isMember)){
                    //点赞->取消点赞
                    Long removed = redisTemplate.opsForSet().remove(key, userId);
                    if (removed != null && removed > 0) {
                        return Result.success("取消点赞成功");
                    } else {
                        return Result.error(Result.ErrorCode.BUSINESS_ERROR, "您还未点赞");
                    }
                }else{
                    //取消点赞->点赞
                    Long added = redisTemplate.opsForSet().add(key, userId);
                    if (added != null && added > 0) {
                        return Result.success("点赞成功");
                    } else {
                        return Result.error(Result.ErrorCode.BUSINESS_ERROR, "操作失败，请重试");
                    }
                }

            } catch (Exception e) {
                log.error("点赞操作异常，正在重试...", e); // 关键！打印完整堆栈
                if (retryCount == 0) {
                    return Result.error(Result.ErrorCode.SERVER_ERROR, "系统异常，请稍后再试");
                }
            }
        }

        return Result.error(Result.ErrorCode.BUSINESS_ERROR, "操作失败，请重试");
    }

    //获取点赞数
    @Override
    public Result<Long> getLikeCount(Long articleId) {
        String key = "article:like:" + articleId;
        Long count = redisTemplate.opsForSet().size(key);
        return Result.success(count != null ? count : 0L);
    }
}
