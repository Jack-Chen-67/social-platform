package com.itcjx.socialplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.socialplatform.VO.CommentVO;
import com.itcjx.socialplatform.entity.Comment;
import com.itcjx.socialplatform.mapper.CommentMapper;
import com.itcjx.socialplatform.service.ICommentService;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CommentMapper commentMapper;

    //发布评论/回复
    @Override
    public Result<Long> addComment(Long articleId, String content, Long parentId, String token) {

        if (articleId == null || content == null || token == null) {
            return Result.error(Result.ErrorCode.PARAM_ERROR);
        }

        // 1. 从Token解析用户ID
        Long userId = jwtTokenUtil.getUserIdFromToken(token);

        // 2. 构建评论对象
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setParentId(parentId); // 可为null

        // 3. 插入数据库
        int rows = commentMapper.insert(comment);
        if (rows <= 0) {
            return Result.error(Result.ErrorCode.ADD_FAILED);
        }
        return Result.success(comment.getId());
    }

    //获取文章下的所有评论（树形结构）
    @Override
    public Result<List<CommentVO>> getComments(Long articleId, Integer pageNum, Integer pageSize) {
        //创建分页构造器
        Page<Comment> page = new Page<>(pageNum, pageSize);

        //构造查询条件
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId)
                .orderByDesc(Comment::getCreatedAt); // 按创建时间倒序
        //执行分页查询
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
        //查询文章下的所有评论
        //List<Comment> comments = commentMapper.selectByArticleId(articleId);
        //构建树形结构
        List<CommentVO> commentVOs = buildTree(commentPage.getRecords());
        return Result.success(commentVOs)
                .addExtra("total", commentPage.getTotal())
                .addExtra("size", commentPage.getSize())
                .addExtra("pages", commentPage.getPages());
    }

    //删除评论
    @Override
    public Result<Void> deleteComment(Long commentId , String token) {
        //非空判断
        if (commentId == null) {
            return Result.error(Result.ErrorCode.PARAM_ERROR,"评论不存在");
        }else if (token == null) {
            return Result.error(Result.ErrorCode.PARAM_ERROR,"用户不存在");
        }

        //获取token里面的用户id
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        //获取评论
        Comment comment = commentMapper.selectById(commentId);
        //判断用户id是否一致
        if (!userId.equals(comment.getUserId())) {
            return Result.error(Result.ErrorCode.DELETE_FAILED,"无权删除此评论");
        }
        //删除评论
        return commentMapper.deleteById(commentId) > 0 ? Result.success() : Result.error(Result.ErrorCode.DELETE_FAILED);
    }


    //构建树形结构
    private List<CommentVO> buildTree(List<Comment> comments) {

        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        // 1. 批量获取用户名
        List<Long> userIds = comments.stream()
                .map(Comment::getUserId)
                .distinct()
                .toList();

        Map<Long, String> usernameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<Map<String, Object>> userRecords = commentMapper.selectUsernamesByIds(userIds);
            userRecords.forEach(record ->
                    usernameMap.put(
                            ((Number) record.get("id")).longValue(),
                            (String) record.get("username")
                    )
            );
        }

        // 构建父节点
        Map<Long, CommentVO> voMap = new HashMap<>();
        List<CommentVO> rootList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(comment, vo);

            vo.setUsername(usernameMap.getOrDefault(comment.getUserId(), "未知用户"));

            voMap.put(comment.getId(), vo);

            if (comment.getParentId() == null || comment.getParentId() == 0) {
                rootList.add(vo);
            }
        }

        // 构建子节点
        for (Comment comment : comments) {
            if (comment.getParentId() != null && comment.getParentId() != 0) {
                CommentVO parent = voMap.get(comment.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    CommentVO child = voMap.get(comment.getId());
                    parent.getChildren().add(child);
                }
            }
        }

        return rootList;
    }

}
