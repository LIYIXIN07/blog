package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MomentRequest {

    private Long id;

    @NotBlank(message = "动态内容不能为空")
    private String content;

    private Integer likes;

    private Boolean published;

    private LocalDateTime createdAt;
}
