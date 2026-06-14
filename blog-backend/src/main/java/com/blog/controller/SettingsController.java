package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.SettingsRequest;
import com.blog.entity.Settings;
import com.blog.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置控制器
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    /**
     * 获取系统设置
     */
    @GetMapping
    public Result<Settings> getSettings() {
        return Result.success(settingsService.getSettings());
    }

    /**
     * 更新系统设置
     */
    @PutMapping
    public Result<Void> updateSettings(@RequestBody SettingsRequest request) {
        settingsService.updateSettings(request);
        return Result.success();
    }
}
