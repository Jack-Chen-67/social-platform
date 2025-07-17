package com.itcjx.socialplatform.controller;

import com.itcjx.socialplatform.VO.CommentVO;
import com.itcjx.socialplatform.service.ICommentService;
import com.itcjx.socialplatform.service.impl.CommentServiceImpl;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentServiceImpl commentService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //发布评论/回复
    @PostMapping("/addComment")
    public Result<Long> addComment(
            @RequestParam Long articleId,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId,
            @RequestHeader("Authorization") String token) {

        // 清理 Bearer 和前后空格
        token = token.replace("Bearer ", "").trim();
        return commentService.addComment(articleId, content, parentId, token);
    }

    //获取文章下的所有评论（树形结构）
    @GetMapping("/getComments")
    public Result<List<CommentVO>> getComments(
            @RequestParam Long articleId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return commentService.getComments(articleId, pageNum, pageSize);
    }

    //删除评论
    @DeleteMapping("/deleteComment/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId,
                                        @RequestHeader("Authorization") String token) {
        // 清理 Bearer 和前后空格
        token = token.replace("Bearer ", "").trim();
        return commentService.deleteComment(commentId, token);
    }
}
