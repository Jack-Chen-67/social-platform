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
        //判断是否点赞
        boolean isLike = redisTemplate.opsForSet().isMember(key, userId);
        if(Boolean.TRUE.equals(isLike)){
            //点赞->取消点赞
            redisTemplate.opsForSet().remove(key, userId);
            return Result.success("取消点赞成功");
        }else{
            //取消点赞->点赞
            redisTemplate.opsForSet().add(key, userId);
            return Result.success("点赞成功");
        }
    }

    //获取点赞数
    @Override
    public Result<Long> getLikeCount(Long articleId) {
        String key = "article:like:" + articleId;
        Long count = redisTemplate.opsForSet().size(key);
        return Result.success(count != null ? count : 0L);
    }
}
