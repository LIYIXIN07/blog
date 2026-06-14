package com.blog.config;

import com.blog.service.AboutInitService;
import com.blog.service.AuthService;
import com.blog.service.CategoryInitService;
import com.blog.service.ImageBedService;
import com.blog.service.SiteSettingInitService;
import com.blog.service.SettingsInitService;
import com.blog.service.SystemService;
import com.blog.service.TagInitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据初始化配置类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final CategoryInitService categoryInitService;
    private final AboutInitService aboutInitService;
    private final SettingsInitService settingsInitService;
    private final SiteSettingInitService siteSettingInitService;
    private final TagInitService tagInitService;
    private final ImageBedService imageBedService;
    private final SystemService systemService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化数据...");
        
        authService.initAdminUser();
        log.info("管理员用户初始化完成");

        categoryInitService.ensureDefaultCategories();
        log.info("默认分类初始化完成");

        aboutInitService.ensureDefaultAboutData();
        log.info("关于页默认数据初始化完成");

        settingsInitService.ensureDefaultSettings();
        log.info("站点设置默认数据初始化完成");

        siteSettingInitService.ensureDefaultSiteSettings();
        log.info("站点设置（NBlog 风格）初始化完成");

        tagInitService.ensureDefaultTags();
        log.info("默认标签初始化完成");

        imageBedService.ensureDefaults();
        systemService.ensureDefaultTasks();
        log.info("图床与定时任务初始化完成");
        
        log.info("数据初始化完成");
    }
}