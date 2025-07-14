package com.itcjx.socialplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcjx.socialplatform.entity.Article;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT * FROM articles WHERE user_id = #{userId} ORDER BY created_at DESC")
    List< Article> selectById(Long userId);
}
