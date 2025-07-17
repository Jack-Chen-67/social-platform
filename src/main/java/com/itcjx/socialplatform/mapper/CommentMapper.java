package com.itcjx.socialplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itcjx.socialplatform.entity.Comment;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface CommentMapper extends BaseMapper<Comment> {

    @Select("select * from comments where article_id = #{articleId}")
    List<Comment> selectByArticleId(Long articleId);

    @Select("select username from users where id = #{userId}")
    String selectUsernameById(Long userId);

    @MapKey("id")
    List<Map<String, Object>> selectUsernamesByIds(@Param("userIds") List<Long> userIds);

}
