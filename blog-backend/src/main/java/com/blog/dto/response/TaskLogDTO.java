package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskLogDTO {

    private Long id;
    private String taskName;
    private Integer status;
    private String message;
    private Long durationMs;
    private LocalDateTime createdAt;
}
