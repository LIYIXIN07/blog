package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.response.PublicSiteInfoDTO;
import com.blog.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/site-info")
@RequiredArgsConstructor
public class PublicSiteInfoController {

    private final SiteSettingService siteSettingService;

    @GetMapping
    public Result<PublicSiteInfoDTO> getSiteInfo() {
        return Result.success(siteSettingService.getPublicSiteInfo());
    }
}
