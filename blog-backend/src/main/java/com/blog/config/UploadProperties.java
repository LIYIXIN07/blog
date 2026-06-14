package com.blog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blog.upload")
public class UploadProperties {

    /**
     * 本地文件存储目录
     */
    private String dir = "uploads";

    /**
     * 对外访问路径前缀
     */
    private String urlPrefix = "/upload";
}
