package com.blog.controller;

import com.blog.common.result.PageResult;
import com.blog.common.result.Result;
import com.blog.dto.request.ArticleQueryRequest;
import com.blog.dto.request.ArticleRequest;
import com.blog.dto.request.ArticleVisibilityRequest;
import com.blog.dto.response.ArchiveStatDTO;
import com.blog.dto.response.ArticleDTO;
import com.blog.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器
 */
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 分页查询文章列表
     */
    @GetMapping
    public Result<PageResult<ArticleDTO>> getArticleList(ArticleQueryRequest request) {
        PageResult<ArticleDTO> result = articleService.getArticleList(request);
        return Result.success(result);
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleDTO> getArticleById(@PathVariable Long id,
                                             @RequestHeader(value = "X-Visitor-Uuid", required = false) String visitorUuid) {
        ArticleDTO article = articleService.getArticleById(id, visitorUuid);
        return Result.success(article);
    }

    /**
     * 创建文章
     */
    @PostMapping
    public Result<Long> createArticle(@Valid @RequestBody ArticleRequest request) {
        Long id = articleService.createArticle(request);
        return Result.success(id);
    }

    /**
     * 更新文章
     */
    @PutMapping
    public Result<Void> updateArticle(@Valid @RequestBody ArticleRequest request) {
        articleService.updateArticle(request);
        return Result.success();
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success();
    }

    /**
     * 批量删除文章
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDeleteArticles(@RequestBody List<Long> ids) {
        articleService.batchDeleteArticles(ids);
        return Result.success();
    }

    /**
     * 增加文章阅读量
     */
    @PostMapping("/{id}/view")
    public Result<Integer> incrementViewCount(@PathVariable Long id) {
        return Result.success(articleService.incrementViewCount(id));
    }

    @PutMapping("/{id}/top")
    public Result<Void> updateTop(@PathVariable Long id, @RequestParam Boolean isTop) {
        articleService.updateTop(id, isTop);
        return Result.success();
    }

    @PutMapping("/{id}/recommend")
    public Result<Void> updateRecommend(@PathVariable Long id, @RequestParam Boolean isRecommend) {
        articleService.updateRecommend(id, isRecommend);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        articleService.updateStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/visibility")
    public Result<Void> updateVisibility(@PathVariable Long id,
                                         @RequestBody ArticleVisibilityRequest request) {
        articleService.updateVisibility(id, request);
        return Result.success();
    }

    /**
     * 获取最新文章
     */
    @GetMapping("/latest")
    public Result<List<ArticleDTO>> getLatestArticles(@RequestParam(defaultValue = "5") Integer limit) {
        List<ArticleDTO> articles = articleService.getLatestArticles(limit);
        return Result.success(articles);
    }

    /**
     * 获取热门文章
     */
    @GetMapping("/hot")
    public Result<List<ArticleDTO>> getHotArticles(@RequestParam(defaultValue = "5") Integer limit) {
        List<ArticleDTO> articles = articleService.getHotArticles(limit);
        return Result.success(articles);
    }

    @GetMapping("/random")
    public Result<List<ArticleDTO>> getRandomArticles(@RequestParam(defaultValue = "5") Integer limit) {
        List<ArticleDTO> articles = articleService.getRandomArticles(limit);
        return Result.success(articles);
    }

    @GetMapping("/archives")
    public Result<List<ArchiveStatDTO>> getArchives() {
        return Result.success(articleService.getArchives());
    }

    @GetMapping("/search")
    public Result<PageResult<ArticleDTO>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(articleService.searchArticles(keyword, pageNum, pageSize));
    }
}