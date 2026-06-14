package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.FriendLinkRequest;
import com.blog.entity.FriendLink;
import com.blog.service.FriendLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 友情链接控制器
 */
@RestController
@RequestMapping("/friend-links")
@RequiredArgsConstructor
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    /**
     * 获取所有友情链接
     */
    @GetMapping
    public Result<List<FriendLink>> getAllFriendLinks() {
        return Result.success(friendLinkService.getAllFriendLinks());
    }

    /**
     * 创建友情链接
     */
    @PostMapping
    public Result<Long> createFriendLink(@Valid @RequestBody FriendLinkRequest request) {
        return Result.success(friendLinkService.createFriendLink(request));
    }

    /**
     * 更新友情链接
     */
    @PutMapping
    public Result<Void> updateFriendLink(@Valid @RequestBody FriendLinkRequest request) {
        friendLinkService.updateFriendLink(request);
        return Result.success();
    }

    /**
     * 删除友情链接
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFriendLink(@PathVariable Long id) {
        friendLinkService.deleteFriendLink(id);
        return Result.success();
    }
}
