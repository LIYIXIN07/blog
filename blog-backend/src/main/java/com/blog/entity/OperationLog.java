package com.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_log")
@EntityListeners(AuditingEntityListener.class)
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "module", length = 50)
    private String module;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "method", length = 10)
    private String method;

    @Column(name = "uri", length = 255)
    private String uri;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "params", columnDefinition = "TEXT")
    private String params;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
