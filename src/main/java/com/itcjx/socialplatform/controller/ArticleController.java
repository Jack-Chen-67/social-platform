package com.itcjx.socialplatform.controller;

import com.itcjx.socialplatform.DTO.ArticleDTO;
import com.itcjx.socialplatform.entity.Article;
import com.itcjx.socialplatform.service.impl.ArticleServiceImpl;
import com.itcjx.socialplatform.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleServiceImpl articleService;

    public ArticleController(ArticleServiceImpl articleService)
    {
        this.articleService = articleService;
    }

    //创建文章
    @PostMapping("/createArticle")
    Result<Long> createArticle(@RequestBody ArticleDTO articleDTO, @RequestHeader("Authorization") String authorizationHeader){
        // 清理 Bearer 和前后空格
        String token = authorizationHeader.replace("Bearer ", "").trim();
        return articleService.createArticle(articleDTO, token);
    }

    //获取文章
    @GetMapping("/getArticle/{id}")
    Result<ArticleDTO> getArticle(@PathVariable Long id){
        return articleService.getArticleById(id);
    }

    //删除文章
    @DeleteMapping("/deleteArticle/{id}")
    Result<Void> deleteArticle(@PathVariable Long id ,@RequestHeader("Authorization") String authorizationHeader){
        // 清理 Bearer 和前后空格
        String token = authorizationHeader.replace("Bearer ", "").trim();
        return articleService.deleteArticle(id,token);
    }

    @GetMapping("/searchWithAI")
    public Result<List<Article>> searchWithAI(@RequestParam String keyword){
        return articleService.searchWithAI(keyword);
    }
}
