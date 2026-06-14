package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.CategoryRequest;
import com.blog.dto.response.CategoryDTO;
import com.blog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping
    public Result<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<Long> createCategory(@Valid @RequestBody CategoryRequest request) {
        Long id = categoryService.createCategory(request);
        return Result.success(id);
    }

    /**
     * 更新分类
     */
    @PutMapping
    public Result<Void> updateCategory(@Valid @RequestBody CategoryRequest request) {
        categoryService.updateCategory(request);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}