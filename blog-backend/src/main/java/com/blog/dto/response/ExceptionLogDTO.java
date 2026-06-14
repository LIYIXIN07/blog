package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionLogDTO {

    private Long id;
    private String username;
    private String uri;
    private String method;
    private String exceptionName;
    private String message;
    private String stackTrace;
    private String ip;
    private LocalDateTime createdAt;
}
