package com.blog.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章查询请求DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleQueryRequest extends PageRequest {

    private String title;
    private Long categoryId;
    private Long tagId;
    private Integer status;
    private Integer year;
    private Integer month;
}