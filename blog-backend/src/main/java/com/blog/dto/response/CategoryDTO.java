package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类响应DTO
 */
@Data
public class CategoryDTO {

    private Long id;
    private String name;
    private String description;
    private Integer sortOrder;
    private Integer articleCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}