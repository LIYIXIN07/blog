package com.blog.controller;

import com.blog.common.result.PageResult;
import com.blog.common.result.Result;
import com.blog.dto.request.MomentRequest;
import com.blog.dto.response.MomentDTO;
import com.blog.service.MomentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @GetMapping("/public")
    public Result<PageResult<MomentDTO>> getPublicMoments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(momentService.getMoments(pageNum, pageSize, true));
    }

    @GetMapping
    public Result<PageResult<MomentDTO>> getMoments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(momentService.getMoments(pageNum, pageSize, false));
    }

    @GetMapping("/{id}")
    public Result<MomentDTO> getMoment(@PathVariable Long id) {
        return Result.success(momentService.getMomentById(id));
    }

    @PostMapping
    public Result<Long> createMoment(@Valid @RequestBody MomentRequest request) {
        return Result.success(momentService.createMoment(request));
    }

    @PutMapping
    public Result<Void> updateMoment(@Valid @RequestBody MomentRequest request) {
        momentService.updateMoment(request);
        return Result.success();
    }

    @PutMapping("/{id}/published")
    public Result<Void> updatePublished(@PathVariable Long id, @RequestParam Boolean published) {
        momentService.updatePublished(id, published);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMoment(@PathVariable Long id) {
        momentService.deleteMoment(id);
        return Result.success();
    }
}
