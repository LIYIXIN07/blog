package com.blog.entity;

import com.blog.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "image_bed_config")
public class ImageBedConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false, unique = true, length = 20)
    private String type;

    @Column(name = "enabled")
    private Boolean enabled = false;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;
}
