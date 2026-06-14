package com.blog.service;

import com.blog.entity.Category;
import com.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 默认分类初始化
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryInitService {

    private record DefaultCategory(String name, String description, int sortOrder) {
    }

    private static final List<DefaultCategory> DEFAULT_CATEGORIES = List.of(
            new DefaultCategory("工作日常", "日常工作记录与分享", 1),
            new DefaultCategory("咖啡日记", "咖啡生活与品鉴记录", 2),
            new DefaultCategory("影视分享", "电影电视剧推荐与观感", 3),
            new DefaultCategory("心情随写", "随想随笔与心情记录", 4),
            new DefaultCategory("学习笔记", "学习过程中的笔记与总结", 5)
    );

    private final CategoryRepository categoryRepository;

    @Transactional
    public void ensureDefaultCategories() {
        int created = 0;
        for (DefaultCategory item : DEFAULT_CATEGORIES) {
            if (categoryRepository.findByName(item.name()).isPresent()) {
                continue;
            }
            Category category = new Category();
            category.setName(item.name());
            category.setDescription(item.description());
            category.setSortOrder(item.sortOrder());
            categoryRepository.save(category);
            created++;
        }
        if (created > 0) {
            log.info("已初始化 {} 个默认分类", created);
        }
    }
}
