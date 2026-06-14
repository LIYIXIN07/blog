package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.config.CacheNames;
import com.blog.dto.request.TagRequest;
import com.blog.dto.response.TagDTO;
import com.blog.entity.Tag;
import com.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务类
 */
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    /**
     * 获取所有标签
     */
    @Cacheable(value = CacheNames.TAGS)
    public List<TagDTO> getAllTags() {
        return tagRepository.findTagWithArticleCount().stream()
                .map(row -> {
                    Tag tag = (Tag) row[0];
                    long articleCount = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    TagDTO dto = new TagDTO();
                    dto.setId(tag.getId());
                    dto.setName(tag.getName());
                    dto.setColor(tag.getColor());
                    dto.setCreatedAt(tag.getCreatedAt());
                    dto.setArticleCount((int) articleCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取标签详情
     */
    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException("标签不存在"));
        return convertToDTO(tag);
    }

    /**
     * 创建标签
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.TAGS, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true)
    })
    @Transactional
    public Long createTag(TagRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new BusinessException("标签名称已存在");
        }
        
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setColor(request.getColor() != null ? request.getColor() : "#e0f2fe");
        
        Tag savedTag = tagRepository.save(tag);
        return savedTag.getId();
    }

    /**
     * 更新标签
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.TAGS, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true)
    })
    @Transactional
    public void updateTag(TagRequest request) {
        Tag tag = tagRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("标签不存在"));
        
        // 检查名称是否重复
        if (!tag.getName().equals(request.getName()) && 
            tagRepository.existsByName(request.getName())) {
            throw new BusinessException("标签名称已存在");
        }
        
        tag.setName(request.getName());
        tag.setColor(request.getColor() != null ? request.getColor() : "#e0f2fe");
        
        tagRepository.save(tag);
    }

    /**
     * 删除标签
     */
    @Caching(evict = {
            @CacheEvict(value = CacheNames.TAGS, allEntries = true),
            @CacheEvict(value = CacheNames.ARTICLE_LIST, allEntries = true)
    })
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException("标签不存在"));
        
        tagRepository.delete(tag);
    }

    /**
     * 转换为DTO
     */
    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setColor(tag.getColor());
        dto.setCreatedAt(tag.getCreatedAt());
        
        int articleCount = tag.getArticles() != null ? tag.getArticles().size() : 0;
        dto.setArticleCount(articleCount);
        
        return dto;
    }
}
