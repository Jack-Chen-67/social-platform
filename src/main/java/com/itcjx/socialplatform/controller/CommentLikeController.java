package com.itcjx.socialplatform.controller;

import com.itcjx.socialplatform.service.ICommentLikeService;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commentLike")
@CrossOrigin(origins = "*")
public class CommentLikeController {

    @Autowired
    private ICommentLikeService commentLikeService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //评论点赞/取消点赞
    @PostMapping
    public Result<String> Commentlike(@RequestParam Long commentId,
                                      @RequestHeader("Authorization") String token){
        // 清理 Bearer 和前后空格
        token = token.replace("Bearer ", "").trim();
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return commentLikeService.Commentlike(commentId, userId);
    }

    // 获取评论点赞数
    @GetMapping("/getCommentLikeCount/{commentId}")
    public Result<Long> getCommentLikeCount(@PathVariable Long commentId){
        return commentLikeService.getCommentLikeCount(commentId);
    }
}
