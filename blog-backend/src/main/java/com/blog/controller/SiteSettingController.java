package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.SiteSettingUpdateRequest;
import com.blog.entity.SiteSetting;
import com.blog.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/site-settings")
@RequiredArgsConstructor
public class SiteSettingController {

    private final SiteSettingService siteSettingService;

    @GetMapping
    public Result<Map<String, List<SiteSetting>>> getSiteSettings() {
        return Result.success(siteSettingService.getGroupedSettings());
    }

    @PutMapping
    public Result<Void> updateSiteSettings(@RequestBody SiteSettingUpdateRequest request) {
        siteSettingService.updateSettings(request);
        return Result.success();
    }
}
