package com.itcjx.socialplatform.service;

import com.itcjx.socialplatform.util.Result;

public interface ILikeService {
    // 点赞和取消点赞
    public Result<String> like(Long articleId,Long userId);

    //获取点赞数
    public Result<Long> getLikeCount(Long articleId);
}
