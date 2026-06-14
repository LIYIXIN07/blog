package com.blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(uploadProperties.getDir()).toAbsolutePath().normalize();
        String location = uploadDir.toUri().toString();
        String prefix = normalizePrefix(uploadProperties.getUrlPrefix());
        registry.addResourceHandler(prefix + "/**")
                .addResourceLocations(location.endsWith("/") ? location : location + "/");
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return "/upload";
        }
        return prefix.startsWith("/") ? prefix : "/" + prefix;
    }
}
