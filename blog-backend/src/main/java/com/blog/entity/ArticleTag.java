package com.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章标签关联实体类
 */
@Data
@Entity
@Table(name = "article_tag")
@IdClass(ArticleTagId.class)
public class ArticleTag {

    @Id
    @Column(name = "article_id")
    private Long articleId;

    @Id
    @Column(name = "tag_id")
    private Long tagId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}