package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogDTO {

    private Long id;
    private String username;
    private String module;
    private String description;
    private String method;
    private String uri;
    private String ip;
    private String params;
    private LocalDateTime createdAt;
}
