package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.AboutRequest;
import com.blog.dto.response.AboutDTO;
import com.blog.service.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/about")
@RequiredArgsConstructor
public class AboutController {

    private final AboutService aboutService;

    @GetMapping
    public Result<AboutDTO> getAbout() {
        return Result.success(aboutService.getAbout());
    }

    @PutMapping
    public Result<Void> updateAbout(@RequestBody AboutRequest request) {
        aboutService.updateAbout(request);
        return Result.success();
    }
}
