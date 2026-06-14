package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.config.CacheNames;
import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryDTO;
import com.blog.entity.Category;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务类
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    /**
     * 获取所有分类
     */
    @Cacheable(value = CacheNames.CATEGORIES)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findCategoryWithArticleCount().stream()
                .map(row -> {
                    Category category = (Category) row[0];
                    long articleCount = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    CategoryDTO dto = new CategoryDTO();
                    dto.setId(category.getId());
                    dto.setName(category.getName());
                    dto.setDescription(category.getDescription());
                    dto.setSortOrder(category.getSortOrder());
                    dto.setCreatedAt(category.getCreatedAt());
                    dto.setArticleCount((int) articleCount);
                    return dto;
                })
                .sorted(Comparator.comparing(CategoryDTO::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                .collect(Collectors.toList());
    }

    /**
     * 获取分类详情
     */
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        return convertToDTO(category);
    }

    /**
     * 创建分类
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true)
    })
    @Transactional
    public Long createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("分类名称已存在");
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSortOrder(resolveSortOrder(request.getSortOrder()));

        Category savedCategory = categoryRepository.save(category);
        return savedCategory.getId();
    }

    /**
     * 更新分类
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true)
    })
    @Transactional
    public void updateCategory(CategoryRequest request) {
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("分类不存在"));
        
        // 检查名称是否重复
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByName(request.getName())) {
            throw new BusinessException("分类名称已存在");
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }

        categoryRepository.save(category);
    }

    /**
     * 删除分类
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true)
    })
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        
        // 检查该分类下是否有已发布文章
        long articleCount = articleRepository.countByCategoryIdAndStatus(id, 1);
        if (articleCount > 0) {
            throw new BusinessException("该分类下还有文章，无法删除");
        }

        categoryRepository.delete(category);
    }

    private int resolveSortOrder(Integer sortOrder) {
        if (sortOrder != null) {
            return sortOrder;
        }
        return categoryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(Category::getSortOrder)
                .filter(order -> order != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    /**
     * 转换为DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSortOrder(category.getSortOrder());
        dto.setCreatedAt(category.getCreatedAt());
        
        // 统计文章数量
        long articleCount = articleRepository.countByCategoryIdAndStatus(category.getId(), 1);
        dto.setArticleCount((int) articleCount);
        
        return dto;
    }
}
