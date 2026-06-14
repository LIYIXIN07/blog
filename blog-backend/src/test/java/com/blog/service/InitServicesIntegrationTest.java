package com.blog.service;

import com.blog.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class InitServicesIntegrationTest {

    @Autowired
    private TagInitService tagInitService;

    @Autowired
    private AboutInitService aboutInitService;

    @Autowired
    private CategoryInitService categoryInitService;

    @Autowired
    private SettingsInitService settingsInitService;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("启动时初始化默认标签")
    void defaultTagsInitialized() {
        tagInitService.ensureDefaultTags();
        assertTrue(tagRepository.count() >= 20, "默认标签数量应不少于 20");
    }

    @Test
    @DisplayName("关于页默认 musicId 为 Childhood Dreams")
    void defaultMusicId() {
        aboutInitService.ensureDefaultAboutData();
        assertTrue(AboutInitService.DEFAULT_MUSIC_ID.equals("1473552803"));
    }

    @Test
    @DisplayName("默认分类与设置可初始化")
    void defaultCategoriesAndSettings() {
        categoryInitService.ensureDefaultCategories();
        settingsInitService.ensureDefaultSettings();
    }
}
