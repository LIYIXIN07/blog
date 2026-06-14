package com.blog.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 文章标签关联ID类
 */
@Data
@EqualsAndHashCode
public class ArticleTagId implements Serializable {

    private Long articleId;
    private Long tagId;
}