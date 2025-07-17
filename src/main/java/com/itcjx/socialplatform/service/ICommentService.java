package com.itcjx.socialplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.socialplatform.VO.CommentVO;
import com.itcjx.socialplatform.entity.Comment;
import com.itcjx.socialplatform.util.Result;

import java.util.List;

public interface ICommentService extends IService<Comment> {

    //发布评论/回复
    Result<Long> addComment(
            Long articleId,
            String content,
            Long parentId,
            String token);
    //获取文章下的所有评论（树形结构）
    Result<List<CommentVO>> getComments(Long commentId, Integer pageNum, Integer pageSize);
    //删除评论
    Result<Void> deleteComment(Long commentId , String token);
}
