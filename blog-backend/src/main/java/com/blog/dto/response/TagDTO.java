package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签响应DTO
 */
@Data
public class TagDTO {

    private Long id;
    private String name;
    private String color;
    private Integer articleCount;
    private LocalDateTime createdAt;
}