package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MomentDTO {

    private Long id;
    private String content;
    private Integer likes;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
