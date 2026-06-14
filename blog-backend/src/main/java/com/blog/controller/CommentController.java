package com.blog.controller;

import com.blog.common.result.PageResult;
import com.blog.common.result.Result;
import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentDTO;
import com.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public Result<PageResult<CommentDTO>> getComments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer pageType,
            @RequestParam(required = false) Long articleId) {
        return Result.success(commentService.getComments(pageNum, pageSize, pageType, articleId));
    }

    @PutMapping("/{id}/published")
    public Result<Void> updatePublished(@PathVariable Long id, @RequestParam Boolean published) {
        commentService.updatePublished(id, published);
        return Result.success();
    }

    @PutMapping("/{id}/notice")
    public Result<Void> updateNotice(@PathVariable Long id, @RequestParam Boolean notice) {
        commentService.updateNotice(id, notice);
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateComment(@Valid @RequestBody CommentRequest request) {
        commentService.updateComment(request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return Result.success();
    }
}
