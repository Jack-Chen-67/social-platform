package com.itcjx.socialplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.socialplatform.DTO.ArticleDTO;
import com.itcjx.socialplatform.controller.UserController;
import com.itcjx.socialplatform.entity.Article;
import com.itcjx.socialplatform.mapper.ArticleMapper;
import com.itcjx.socialplatform.service.IArticleService;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import com.itcjx.socialplatform.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private ArticleMapper articleMapper;
    private final JwtTokenUtil jwtTokenUtil; // 用于解析Token中的用户ID

    public ArticleServiceImpl(ArticleMapper articleMapper, JwtTokenUtil jwtTokenUtil) {
        this.articleMapper = articleMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    // 创建文章
    @Override
    public Result<Long> createArticle(ArticleDTO articleDTO , String token) {
        try{
            Article article = new Article();
            article.setUserId(jwtTokenUtil.getUserIdFromToken(token.trim()));
            article.setTitle(articleDTO.getTitle());
            article.setContent(articleDTO.getContent());
            articleMapper.insert(article);
            return Result.success(article.getId());
        }catch (Exception e){
            log.error("创建文章失败：{}", e.getMessage());
            return Result.error(Result.ErrorCode.ADD_FAILED);
        }
    }

    // 获取文章
    @Override
    public Result<ArticleDTO> getArticleById(Long id) {
        // 查询文章
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        ArticleDTO dto = new ArticleDTO();
        dto.setContent(article.getContent());
        dto.setTitle(article.getTitle());
        return Result.success(dto);
    }

    // 删除文章
    @Override
    public Result<Void> deleteArticle(Long ArticleId, String token) {
        //获取token里面的用户id
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        //判断用户id是否一致
        Article article = (Article) articleMapper.selectById(ArticleId);
        if (article == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND,"文章不存在");
        }
        if (!userId.equals(article.getUserId())) {
            return Result.error(Result.ErrorCode.DELETE_FAILED,"无权删除此文章");
        }
        //删除文章
        articleMapper.deleteById(ArticleId);
        return Result.success();
    }
}
