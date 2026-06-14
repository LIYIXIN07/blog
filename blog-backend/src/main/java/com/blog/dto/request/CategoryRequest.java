package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 分类请求DTO
 */
@Data
public class CategoryRequest {

    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private String description;

    private Integer sortOrder;
}