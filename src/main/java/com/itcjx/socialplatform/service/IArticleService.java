package com.itcjx.socialplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itcjx.socialplatform.DTO.ArticleDTO;
import com.itcjx.socialplatform.entity.Article;
import com.itcjx.socialplatform.util.Result;

public interface IArticleService extends IService<Article> {

    //发布文章
    Result<Long> createArticle(ArticleDTO articleDTO, String token);
    //获取文章详情
    Result<ArticleDTO> getArticleById(Long id);
    //删除文章（需作者）
    Result<Void> deleteArticle(Long id , String token);
}
