package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "settings")
public class Settings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", length = 100)
    private String siteName;

    @Column(name = "site_description", length = 255)
    private String siteDescription;

    @Column(name = "author_name", length = 50)
    private String authorName;

    @Column(name = "author_avatar", length = 255)
    private String authorAvatar;

    @Column(name = "author_bio", length = 500)
    private String authorBio;

    @Column(name = "github", length = 255)
    private String github;

    @Column(name = "telegram", length = 255)
    private String telegram;

    @Column(name = "twitter", length = 255)
    private String twitter;

    @Column(name = "gitee", length = 255)
    private String gitee;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "icp", length = 100)
    private String icp;

    @Column(name = "site_start_date")
    private java.time.LocalDate siteStartDate;
}