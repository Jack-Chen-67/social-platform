package com.itcjx.socialplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itcjx.socialplatform.entity.Article;
import com.itcjx.socialplatform.mapper.ArticleMapper;
import com.itcjx.socialplatform.service.IArticleService;
import com.itcjx.socialplatform.util.JwtTokenUtil;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {

    private ArticleMapper articleMapper;
    private final JwtTokenUtil jwtTokenUtil; // 用于解析Token中的用户ID

    public ArticleServiceImpl(ArticleMapper articleMapper, JwtTokenUtil jwtTokenUtil) {
        this.articleMapper = articleMapper;
        this.jwtTokenUtil = jwtTokenUtil;
    }

}
