package com.itcjx.socialplatform.entity;

// 用户兴趣向量（Redis存储）
public class UserInterestVector {
    private Long userId;
    private float[] vector;  // 文章向量
    private Double weight;   // 兴趣权重（根据点赞/收藏等行为调整）
}

