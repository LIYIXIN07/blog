package com.blog.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 站点设置（NBlog 风格 key-value 配置）
 */
@Data
@Entity
@Table(name = "site_setting")
public class SiteSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_en", nullable = false, length = 50)
    private String nameEn;

    @Column(name = "name_zh", nullable = false, length = 50)
    private String nameZh;

    @Column(columnDefinition = "TEXT")
    private String value;

    @Column(nullable = false)
    private Integer type;
}
