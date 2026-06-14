package com.blog.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 启动时打印关键配置，便于排查宝塔环境变量覆盖问题
 */
@Slf4j
@Configuration
public class StartupConfigLogger {

    @Bean
    CommandLineRunner logStartupConfig(Environment env) {
        return args -> {
            String profile = String.join(",", env.getActiveProfiles());
            if (profile.isBlank()) {
                profile = String.join(",", env.getDefaultProfiles());
            }
            String url = env.getProperty("spring.datasource.url", "未配置");
            String user = env.getProperty("spring.datasource.username", "未配置");
            log.info("======== 启动配置 ========");
            log.info("Active Profile: {}", profile);
            log.info("Datasource URL: {}", url);
            log.info("Datasource User: {}", user);
            log.info("Redis Host: {}:{}", env.getProperty("spring.data.redis.host", "?"),
                    env.getProperty("spring.data.redis.port", "?"));
            log.info("==========================");
        };
    }
}
