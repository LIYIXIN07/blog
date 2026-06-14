package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "ip", length = 64)
    private String ip;

    @Column(name = "ip_source", length = 255)
    private String ipSource;

    @Column(name = "published")
    private Boolean published = true;

    @Column(name = "admin_comment")
    private Boolean adminComment = false;

    /** 0=文章 1=关于我 2=友人帐 */
    @Column(name = "page_type")
    private Integer pageType = 0;

    @Column(name = "notice")
    private Boolean notice = false;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "qq", length = 20)
    private String qq;
}
