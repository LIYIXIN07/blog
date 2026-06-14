package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章响应DTO
 */
@Data
public class ArticleDTO {

    private Long id;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private List<TagDTO> tags;
    private String author;
    private Integer viewCount;
    private Integer status;
    private Boolean isTop;
    private Boolean isRecommend;
    private String password;
    private Boolean appreciation;
    private Boolean commentEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}