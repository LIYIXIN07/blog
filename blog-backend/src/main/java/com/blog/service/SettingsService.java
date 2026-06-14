package com.blog.service;

import com.blog.config.CacheNames;
import com.blog.dto.request.SettingsRequest;
import com.blog.entity.Settings;
import com.blog.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;

    @Cacheable(value = CacheNames.SETTINGS, key = "'site'")
    public Settings getSettings() {
        return settingsRepository.findById(1L)
                .orElseGet(() -> {
                    Settings defaultSettings = new Settings();
                    defaultSettings.setId(1L);
                    defaultSettings.setSiteName("个人博客");
                    defaultSettings.setSiteDescription("记录技术成长，分享学习心得");
                    defaultSettings.setAuthorName("DollMeowOnly");
                    defaultSettings.setAuthorBio("热爱技术，乐于分享");
                    return settingsRepository.save(defaultSettings);
                });
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.SETTINGS, key = "'site'"),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    })
    @Transactional
    public void updateSettings(SettingsRequest request) {
        Settings settings = settingsRepository.findById(1L)
                .orElseGet(() -> {
                    Settings created = new Settings();
                    created.setId(1L);
                    return created;
                });

        settings.setSiteName(request.getSiteName());
        settings.setSiteDescription(request.getSiteDescription());
        settings.setAuthorName(request.getAuthorName());
        settings.setAuthorAvatar(request.getAuthorAvatar());
        settings.setAuthorBio(request.getAuthorBio());
        settings.setGithub(request.getGithub());
        settings.setTelegram(request.getTelegram());
        settings.setTwitter(request.getTwitter());
        settings.setGitee(request.getGitee());
        settings.setEmail(request.getEmail());
        settings.setIcp(request.getIcp());
        settings.setSiteStartDate(request.getSiteStartDate());

        settingsRepository.save(settings);
    }
}
