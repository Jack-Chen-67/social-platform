package com.itcjx.socialplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcjx.socialplatform.entity.Article;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    // 根据用户ID查询文章
    @Select("SELECT * FROM articles WHERE user_id = #{userId} ORDER BY created_at DESC")
    List< Article> selectByUserId(Long userId);

    // 根据文章ID查询文章
    @Select("SELECT * FROM articles WHERE id = #{id}")
    Article selectById(Long id);

    // 自定义多关键词查询
    //@Select("SELECT * FROM articles WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Article> searchByKeyword(@Param("keyword") String keyword);

    // 多关键词查询
    List<Article> searchByKeywords(@Param("keywords") List<String> keywords);
}
