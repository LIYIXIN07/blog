package com.blog.dto.request;

import lombok.Data;

@Data
public class ScheduledTaskRequest {

    private String beanName;
    private String methodName;
    private String params;
    private String cronExpression;
    private Integer status;
    private String remark;
}
