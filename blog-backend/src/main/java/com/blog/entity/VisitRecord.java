package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 访客记录
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "visit_record")
public class VisitRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_uuid", nullable = false, length = 64)
    private String visitorUuid;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "ip_source", length = 255)
    private String ipSource;

    @Column(name = "os", length = 100)
    private String os;

    @Column(name = "browser", length = 100)
    private String browser;

    @Column(name = "pv")
    private Integer pv = 1;
}
