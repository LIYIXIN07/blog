package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "article")
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_image", length = 2048)
    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "article_tag",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Column(name = "author", length = 50)
    private String author;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "status")
    private Integer status = 0; // 0: 草稿, 1: 已发布

    @Column(name = "is_top")
    private Boolean isTop = false;

    @Column(name = "is_recommend")
    private Boolean isRecommend = false;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "appreciation")
    private Boolean appreciation = false;

    @Column(name = "comment_enabled")
    private Boolean commentEnabled = true;

    /**
     * 添加标签
     */
    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getArticles().add(this);
    }

    /**
     * 移除标签
     */
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getArticles().remove(this);
    }
}