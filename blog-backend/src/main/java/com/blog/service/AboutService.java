package com.blog.service;

import com.blog.config.CacheNames;
import com.blog.dto.request.AboutRequest;
import com.blog.dto.response.AboutDTO;
import com.blog.entity.AboutPage;
import com.blog.repository.AboutPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AboutService {

    private final AboutPageRepository aboutPageRepository;

    @Cacheable(value = CacheNames.ABOUT, key = "'page'")
    public AboutDTO getAbout() {
        return toDTO(getOrCreate());
    }

    @CacheEvict(value = CacheNames.ABOUT, key = "'page'")
    @Transactional
    public void updateAbout(AboutRequest request) {
        AboutPage page = getOrCreate();
        if (request.getTitle() != null) {
            page.setTitle(request.getTitle());
        }
        if (request.getMusicId() != null) {
            page.setMusicId(request.getMusicId());
        }
        if (request.getContent() != null) {
            page.setContent(request.getContent());
        }
        if (request.getCommentEnabled() != null) {
            page.setCommentEnabled(request.getCommentEnabled());
        }
        aboutPageRepository.save(page);
    }

    public boolean isCommentEnabled() {
        AboutPage page = aboutPageRepository.findById(1L).orElse(null);
        return page == null || !Boolean.FALSE.equals(page.getCommentEnabled());
    }

    private AboutPage getOrCreate() {
        return aboutPageRepository.findById(1L).orElseGet(() -> {
            AboutPage page = new AboutPage();
            page.setId(1L);
            page.setTitle("关于我");
            page.setContent("");
            page.setMusicId(AboutInitService.DEFAULT_MUSIC_ID);
            page.setCommentEnabled(true);
            return aboutPageRepository.save(page);
        });
    }

    private AboutDTO toDTO(AboutPage page) {
        AboutDTO dto = new AboutDTO();
        dto.setTitle(page.getTitle());
        dto.setMusicId(page.getMusicId());
        dto.setContent(page.getContent());
        dto.setCommentEnabled(page.getCommentEnabled());
        return dto;
    }
}
