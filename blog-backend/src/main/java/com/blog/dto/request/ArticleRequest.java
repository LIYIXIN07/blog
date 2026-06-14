package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 文章请求DTO
 */
@Data
public class ArticleRequest {

    private Long id;

    @NotBlank(message = "文章标题不能为空")
    private String title;

    @NotBlank(message = "文章摘要不能为空")
    private String summary;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Size(max = 2048, message = "首图 URL 不能超过 2048 个字符")
    private String coverImage;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private List<Long> tagIds;

    @NotNull(message = "状态不能为空")
    private Integer status; // 0: 草稿, 1: 已发布

    private Boolean isTop;

    private Boolean isRecommend;
}