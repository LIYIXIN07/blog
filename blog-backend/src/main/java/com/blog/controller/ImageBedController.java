package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.ImageBedConfigRequest;
import com.blog.dto.response.ImageBedConfigDTO;
import com.blog.service.ImageBedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/image-bed")
@RequiredArgsConstructor
public class ImageBedController {

    private final ImageBedService imageBedService;

    @GetMapping
    public Result<List<ImageBedConfigDTO>> getAllConfigs() {
        return Result.success(imageBedService.getAllConfigs());
    }

    @GetMapping("/{type}")
    public Result<ImageBedConfigDTO> getConfig(@PathVariable String type) {
        return Result.success(imageBedService.getConfig(type));
    }

    @PutMapping("/{type}")
    public Result<Void> updateConfig(@PathVariable String type,
                                     @RequestBody ImageBedConfigRequest request,
                                     Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "admin";
        imageBedService.updateConfig(type, request, username);
        return Result.success();
    }
}
