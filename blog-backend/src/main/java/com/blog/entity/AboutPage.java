package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "about_page")
public class AboutPage extends BaseEntity {

    @Id
    private Long id;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "music_id", length = 50)
    private String musicId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "comment_enabled")
    private Boolean commentEnabled = true;
}
