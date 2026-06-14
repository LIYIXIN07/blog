package com.blog.controller;

import com.blog.common.result.Result;
import com.blog.dto.request.CommentSubmitRequest;
import com.blog.dto.response.PublicCommentPageDTO;
import com.blog.service.CommentService;
import com.blog.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments/public")
@RequiredArgsConstructor
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public Result<PublicCommentPageDTO> getPublicComments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Integer pageType,
            @RequestParam(required = false) Long articleId) {
        return Result.success(commentService.getPublicComments(pageNum, pageSize, pageType, articleId));
    }

    @PostMapping
    public Result<Void> submitComment(@Valid @RequestBody CommentSubmitRequest request,
                                      HttpServletRequest httpRequest) {
        String ip = IpUtils.getClientIp(httpRequest);
        commentService.createPublicComment(request, ip);
        return Result.success();
    }
}
