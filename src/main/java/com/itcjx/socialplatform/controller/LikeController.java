package com.itcjx.socialplatform.controller;

import com.itcjx.socialplatform.service.ILikeService;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private ILikeService likeService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //点赞和取消点赞
    @PostMapping
    public Result<String> like(@RequestParam Long articleId,@RequestHeader("Authorization") String token){
        // 清理 Bearer 和前后空格
        token = token.replace("Bearer ", "").trim();
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        return likeService.like(articleId,userId);
    }

    //获取点赞数
    @GetMapping("/{articleId}")
    public Result<Long> getLikeCount(@PathVariable Long articleId){
        return likeService.getLikeCount(articleId);
    }
}
