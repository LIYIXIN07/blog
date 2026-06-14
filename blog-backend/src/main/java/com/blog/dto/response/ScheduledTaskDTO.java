package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduledTaskDTO {

    private Long id;
    private String beanName;
    private String methodName;
    private String params;
    private String cronExpression;
    private Integer status;
    private String remark;
    private LocalDateTime createdAt;
}
