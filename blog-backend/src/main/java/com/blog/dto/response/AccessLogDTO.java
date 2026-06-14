package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccessLogDTO {

    private Long id;
    private String visitorUuid;
    private String uri;
    private String ip;
    private String ipSource;
    private String os;
    private String browser;
    private LocalDateTime createdAt;
}
