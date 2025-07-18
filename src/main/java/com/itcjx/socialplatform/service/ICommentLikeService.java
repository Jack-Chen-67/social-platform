package com.itcjx.socialplatform.service;

import com.itcjx.socialplatform.util.Result;

public interface ICommentLikeService {
    // 评论点赞和取消点赞
    public Result<String> Commentlike(Long commentId, Long userId);

    //获取点赞数
    public Result<Long> getCommentLikeCount(Long commentId);
}
