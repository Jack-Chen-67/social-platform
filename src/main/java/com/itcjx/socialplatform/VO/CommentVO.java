package com.itcjx.socialplatform.VO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentVO {
    private Long id;               // 评论ID
    private Long userId;           // 评论用户ID
    private String username;       // 用户名
    private String content;        // 评论内容
    private LocalDateTime createdAt; // 创建时间
    private Long parentId;         // 父评论ID（用于构建树）
    private List<CommentVO> children = new ArrayList<>(); // 子评论列表（用于树形结构）

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<CommentVO> getChildren() {
        return children;
    }

    public void setChildren(List<CommentVO> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "CommentVO{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", parentId=" + parentId +
                ", children=" + children +
                '}';
    }
}
