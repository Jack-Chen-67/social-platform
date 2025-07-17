package com.itcjx.socialplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("comments")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;      // 评论者ID
    private Long articleId;   // 文章ID
    private String content;   // 评论内容
    private Long parentId;    // 父评论ID（可选）
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", articleId=" + articleId +
                ", content='" + content + '\'' +
                ", parentId=" + parentId +
                ", createdAt=" + createdAt +
                '}';
    }
}
