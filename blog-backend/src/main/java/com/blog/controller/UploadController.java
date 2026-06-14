package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.response.UploadResponse;
import com.blog.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping
    public Result<UploadResponse> upload(@RequestParam("file") MultipartFile file,
                                         HttpServletRequest request) {
        return Result.success(uploadService.upload(file, request));
    }
}
