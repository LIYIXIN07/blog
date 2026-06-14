package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LoginLogDTO {

    private Long id;
    private String username;
    private String ip;
    private String ipSource;
    private String os;
    private String browser;
    private Integer status;
    private String message;
    private LocalDateTime createdAt;
}
