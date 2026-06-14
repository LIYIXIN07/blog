package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.PageResult;
import com.blog.config.CacheNames;
import com.blog.dto.request.ArticleRequest;
import com.blog.dto.request.ArticleVisibilityRequest;
import com.blog.dto.request.ArticleQueryRequest;
import com.blog.dto.response.ArchiveStatDTO;
import com.blog.dto.response.ArticleDTO;
import com.blog.dto.response.TagDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.TagRepository;
import com.blog.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章服务类
 */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final RateLimitService rateLimitService;

    @Lazy
    @Autowired
    private ArticleService self;

    /**
     * 分页查询文章列表
     */
    @Cacheable(
            value = CacheNames.ARTICLE_LIST,
            key = "#request.pageNum + '-' + #request.pageSize + '-' + (#request.status != null ? #request.status : 1) + '-' + (#request.categoryId != null ? #request.categoryId : 0) + '-' + (#request.tagId != null ? #request.tagId : 0) + '-' + (#request.title != null ? #request.title : '') + '-' + (#request.year != null ? #request.year : 0) + '-' + (#request.month != null ? #request.month : 0)",
            condition = "#request.status == null || #request.status == 1"
    )
    public PageResult<ArticleDTO> getArticleList(ArticleQueryRequest request) {
        normalizePageRequest(request);
        if (!SecurityUtils.isAdmin()) {
            request.setStatus(1);
        }

        Pageable pageable = PageRequest.of(
                request.getPageNum() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "isTop")
                        .and(Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        Page<Article> page;
        boolean adminAllStatus = SecurityUtils.isAdmin() && request.getStatus() == null;

        if (adminAllStatus) {
            if (request.getCategoryId() != null) {
                page = articleRepository.findByCategoryId(request.getCategoryId(), pageable);
            } else if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                page = articleRepository.findByTitleContaining(request.getTitle(), pageable);
            } else {
                page = articleRepository.findAll(pageable);
            }
        } else {
            Integer status = request.getStatus() != null ? request.getStatus() : 1;

            if (request.getYear() != null && request.getMonth() != null) {
                page = articleRepository.findByYearMonthAndStatus(
                        request.getYear(), request.getMonth(), status, pageable);
            } else if (request.getTitle() != null && !request.getTitle().isEmpty()) {
                page = articleRepository.findByTitleContainingAndStatus(
                        request.getTitle(), status, pageable);
            } else if (request.getCategoryId() != null) {
                page = articleRepository.findByCategoryIdAndStatus(
                        request.getCategoryId(), status, pageable);
            } else if (request.getTagId() != null) {
                page = articleRepository.findByTagIdAndStatus(
                        request.getTagId(), status, pageable);
            } else {
                page = articleRepository.findByStatus(status, pageable);
            }
        }
        
        List<ArticleDTO> list = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(list, page.getTotalElements(), request.getPageNum(), request.getPageSize());
    }

    /**
     * 获取文章详情（前台访问时自动统计阅读量）
     */
    public ArticleDTO getArticleById(Long id, String visitorUuid) {
        Article article = articleRepository.findByIdWithCategoryAndTags(id)
                .orElseThrow(() -> new BusinessException("文章不存在"));
        if (!SecurityUtils.isAdmin() && !Integer.valueOf(1).equals(article.getStatus())) {
            throw new BusinessException("文章不存在");
        }

        ArticleDTO dto = convertToDTO(article);
        if (!SecurityUtils.isAdmin()
                && Integer.valueOf(1).equals(article.getStatus())
                && shouldCountArticleView(id, visitorUuid)) {
            Integer viewCount = self.incrementViewCount(id);
            dto.setViewCount(viewCount);
        }
        return dto;
    }

    private boolean shouldCountArticleView(Long articleId, String visitorUuid) {
        if (visitorUuid == null || visitorUuid.isBlank()) {
            return true;
        }
        String key = "article-view:" + articleId + ":" + visitorUuid.trim();
        return rateLimitService.tryAcquire(key, 1, Duration.ofMinutes(30));
    }

    /**
     * 创建文章
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_RANDOM, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_ARCHIVES, allEntries = true),
            @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheNames.TAGS, allEntries = true),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public Long createArticle(ArticleRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(blankToNull(request.getCoverImage()));
        article.setAuthor("博主");
        article.setViewCount(0);
        article.setStatus(request.getStatus());
        article.setIsTop(Boolean.TRUE.equals(request.getIsTop()));
        article.setIsRecommend(Boolean.TRUE.equals(request.getIsRecommend()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("分类不存在"));
        article.setCategory(category);
        
        // 设置标签
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.getTagIds());
            tags.forEach(article::addTag);
        }
        
        Article savedArticle = articleRepository.save(article);
        return savedArticle.getId();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    /**
     * 更新文章
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_RANDOM, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_ARCHIVES, allEntries = true),
            @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheNames.TAGS, allEntries = true),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public void updateArticle(ArticleRequest request) {
        Article article = articleRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("文章不存在"));
        
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(blankToNull(request.getCoverImage()));
        article.setStatus(request.getStatus());
        if (request.getIsTop() != null) {
            article.setIsTop(request.getIsTop());
        }
        if (request.getIsRecommend() != null) {
            article.setIsRecommend(request.getIsRecommend());
        }
        
        // 更新分类
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("分类不存在"));
        article.setCategory(category);
        
        // 更新标签
        if (request.getTagIds() != null) {
            new ArrayList<>(article.getTags()).forEach(article::removeTag);
            if (!request.getTagIds().isEmpty()) {
                tagRepository.findAllById(request.getTagIds()).forEach(article::addTag);
            }
        }
        
        articleRepository.save(article);
    }

    /**
     * 删除文章
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_RANDOM, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_ARCHIVES, allEntries = true),
            @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheNames.TAGS, allEntries = true),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在"));
        articleRepository.delete(article);
    }

    /**
     * 批量删除文章
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_RANDOM, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_ARCHIVES, allEntries = true),
            @CacheEvict(value = CacheNames.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheNames.TAGS, allEntries = true),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public void batchDeleteArticles(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的文章");
        }
        if (ids.size() > 100) {
            throw new BusinessException("单次最多删除100篇文章");
        }
        articleRepository.deleteAllById(ids);
    }

    /**
     * 增加文章阅读量
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_RANDOM, allEntries = true),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public Integer incrementViewCount(Long id) {
        int updated = articleRepository.incrementViewCount(id);
        if (updated == 0) {
            throw new BusinessException("文章不存在或未发布");
        }
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在"));
        return article.getViewCount() != null ? article.getViewCount() : 0;
    }

    /**
     * 获取最新文章
     */
    @Cacheable(value = CacheNames.ARTICLE_LATEST, key = "#limit != null ? #limit : 5")
    public List<ArticleDTO> getLatestArticles(Integer limit) {
        int size = normalizeLimit(limit);
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Article> articles = articleRepository.findLatestArticles(pageable);
        return articles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * 获取热门文章
     */
    @Cacheable(value = CacheNames.ARTICLE_HOT, key = "#limit != null ? #limit : 5")
    public List<ArticleDTO> getHotArticles(Integer limit) {
        int size = normalizeLimit(limit);
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        List<Article> articles = articleRepository.findHotArticles(pageable);
        return articles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Cacheable(value = CacheNames.ARTICLE_RANDOM, key = "#limit != null ? #limit : 5")
    public List<ArticleDTO> getRandomArticles(Integer limit) {
        List<Article> articles = articleRepository.findRandomArticles(normalizeLimit(limit));
        return articles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Cacheable(value = CacheNames.ARTICLE_ARCHIVES)
    public List<ArchiveStatDTO> getArchives() {
        return articleRepository.findArchiveStats().stream().map(row -> {
            ArchiveStatDTO dto = new ArchiveStatDTO();
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            dto.setYear(year);
            dto.setMonth(month);
            dto.setLabel(String.format("%d年%02d月", year, month));
            dto.setCount(((Number) row[2]).longValue());
            return dto;
        }).collect(Collectors.toList());
    }

    @Cacheable(
            value = CacheNames.ARTICLE_SEARCH,
            key = "#keyword + '-' + #pageNum + '-' + #pageSize"
    )
    public PageResult<ArticleDTO> searchArticles(String keyword, Integer pageNum, Integer pageSize) {
        ArticleQueryRequest request = new ArticleQueryRequest();
        request.setTitle(keyword);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        request.setStatus(1);
        return self.getArticleList(request);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true)
    })
    @Transactional
    public void updateTop(Long id, Boolean isTop) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在"));
        article.setIsTop(Boolean.TRUE.equals(isTop));
        articleRepository.save(article);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true)
    })
    @Transactional
    public void updateRecommend(Long id, Boolean isRecommend) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在"));
        article.setIsRecommend(Boolean.TRUE.equals(isRecommend));
        articleRepository.save(article);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值无效");
        }
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在"));
        article.setStatus(status);
        articleRepository.save(article);
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_SEARCH, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LATEST, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_HOT, allEntries = true),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public void updateVisibility(Long id, ArticleVisibilityRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("文章不存在"));

        boolean published = Boolean.TRUE.equals(request.getPublished());
        article.setStatus(published ? 1 : 0);
        article.setAppreciation(Boolean.TRUE.equals(request.getAppreciation()));
        article.setIsRecommend(Boolean.TRUE.equals(request.getIsRecommend()));
        article.setCommentEnabled(request.getCommentEnabled() == null || request.getCommentEnabled());
        article.setIsTop(Boolean.TRUE.equals(request.getIsTop()));
        article.setPassword(blankToNull(request.getPassword()));

        if (!published) {
            article.setAppreciation(false);
            article.setIsRecommend(false);
            article.setCommentEnabled(false);
            article.setIsTop(false);
            article.setPassword(null);
        }

        articleRepository.save(article);
    }

    private void normalizePageRequest(ArticleQueryRequest request) {
        if (request.getPageNum() == null || request.getPageNum() < 1) {
            request.setPageNum(1);
        }
        if (request.getPageSize() == null || request.getPageSize() < 1) {
            request.setPageSize(10);
        }
        if (request.getPageSize() > 100) {
            request.setPageSize(100);
        }
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return 5;
        }
        return Math.min(limit, 50);
    }

    /**
     * 转换为DTO
     */
    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSummary(article.getSummary());
        dto.setContent(article.getContent());
        dto.setCoverImage(article.getCoverImage());
        dto.setCategoryId(article.getCategory() != null ? article.getCategory().getId() : null);
        dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
        dto.setAuthor(article.getAuthor());
        dto.setViewCount(article.getViewCount());
        dto.setStatus(article.getStatus());
        dto.setIsTop(Boolean.TRUE.equals(article.getIsTop()));
        dto.setIsRecommend(Boolean.TRUE.equals(article.getIsRecommend()));
        dto.setPassword(article.getPassword());
        dto.setAppreciation(Boolean.TRUE.equals(article.getAppreciation()));
        dto.setCommentEnabled(article.getCommentEnabled() == null || article.getCommentEnabled());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        
        // 转换标签
        List<TagDTO> tagDTOs = new ArrayList<>();
        if (article.getTags() != null) {
            tagDTOs = article.getTags().stream().map(tag -> {
                TagDTO tagDTO = new TagDTO();
                tagDTO.setId(tag.getId());
                tagDTO.setName(tag.getName());
                tagDTO.setColor(tag.getColor());
                return tagDTO;
            }).collect(Collectors.toList());
        }
        dto.setTags(tagDTOs);
        
        return dto;
    }
}