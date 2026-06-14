package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 友情链接实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "friend_link")
public class FriendLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;
}