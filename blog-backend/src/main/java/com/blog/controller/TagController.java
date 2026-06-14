package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.TagRequest;
import com.blog.dto.response.TagDTO;
import com.blog.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取所有标签
     */
    @GetMapping
    public Result<List<TagDTO>> getAllTags() {
        List<TagDTO> tags = tagService.getAllTags();
        return Result.success(tags);
    }

    /**
     * 获取标签详情
     */
    @GetMapping("/{id}")
    public Result<TagDTO> getTagById(@PathVariable Long id) {
        TagDTO tag = tagService.getTagById(id);
        return Result.success(tag);
    }

    /**
     * 创建标签
     */
    @PostMapping
    public Result<Long> createTag(@Valid @RequestBody TagRequest request) {
        Long id = tagService.createTag(request);
        return Result.success(id);
    }

    /**
     * 更新标签
     */
    @PutMapping
    public Result<Void> updateTag(@Valid @RequestBody TagRequest request) {
        tagService.updateTag(request);
        return Result.success();
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}