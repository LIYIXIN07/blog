package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 友情链接请求DTO
 */
@Data
public class FriendLinkRequest {

    private Long id;

    @NotBlank(message = "链接名称不能为空")
    private String name;

    @NotBlank(message = "链接地址不能为空")
    private String url;

    private String avatar;

    private String description;

    private Integer sortOrder;
}