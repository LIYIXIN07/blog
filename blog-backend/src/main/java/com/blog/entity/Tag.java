package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tag")
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "color", length = 20)
    private String color;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();
}