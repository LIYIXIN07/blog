package com.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "access_log")
@EntityListeners(AuditingEntityListener.class)
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_uuid", length = 64)
    private String visitorUuid;

    @Column(name = "uri", length = 255)
    private String uri;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "ip_source", length = 255)
    private String ipSource;

    @Column(name = "os", length = 100)
    private String os;

    @Column(name = "browser", length = 100)
    private String browser;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
