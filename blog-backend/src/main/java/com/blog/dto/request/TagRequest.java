package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签请求DTO
 */
@Data
public class TagRequest {

    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String name;

    private String color;
}