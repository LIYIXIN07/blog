package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "scheduled_task")
public class ScheduledTask extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "job_key", nullable = false, length = 100)
    private String jobKey;

    @Column(name = "bean_name", length = 100)
    private String beanName;

    @Column(name = "method_name", length = 100)
    private String methodName;

    @Column(name = "params", length = 255)
    private String params;

    @Column(name = "cron_expression", length = 50)
    private String cronExpression;

    @Column(name = "status")
    private Integer status = 0;

    @Column(name = "remark", length = 255)
    private String remark;

    @Column(name = "last_run_time")
    private LocalDateTime lastRunTime;
}
