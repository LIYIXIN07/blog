package com.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blog")
public class BlogSiteProperties {

    /**
     * 前台站点根 URL，用于 sitemap 等 SEO 输出（不含 /api）
     */
    private String siteUrl = "http://localhost:3000";
}
