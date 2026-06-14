package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.config.CacheNames;
import com.blog.constant.SiteSettingConstants;
import com.blog.dto.request.SiteSettingUpdateRequest;
import com.blog.dto.response.BadgeDTO;
import com.blog.dto.response.CopyrightDTO;
import com.blog.dto.response.PublicSiteInfoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.blog.entity.Settings;
import com.blog.entity.SiteSetting;
import com.blog.repository.SettingsRepository;
import com.blog.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;
    private final SettingsRepository settingsRepository;
    private final ObjectMapper objectMapper;

    public Map<String, List<SiteSetting>> getGroupedSettings() {
        List<SiteSetting> all = siteSettingRepository.findAllByOrderByIdAsc();
        List<SiteSetting> type1 = new ArrayList<>();
        List<SiteSetting> type2 = new ArrayList<>();
        List<SiteSetting> type3 = new ArrayList<>();

        for (SiteSetting setting : all) {
            switch (setting.getType()) {
                case SiteSettingConstants.TYPE_BASIC -> type1.add(setting);
                case SiteSettingConstants.TYPE_PROFILE -> type2.add(setting);
                case SiteSettingConstants.TYPE_BADGE -> type3.add(setting);
                default -> {
                }
            }
        }

        Map<String, List<SiteSetting>> result = new HashMap<>(4);
        result.put("type1", type1);
        result.put("type2", type2);
        result.put("type3", type3);
        return result;
    }

    @Caching(evict = {
            @CacheEvict(value = CacheNames.SETTINGS, key = "'site'"),
            @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'"),
            @CacheEvict(value = CacheNames.SITE_INFO, key = "'public'")
    })
    @Transactional
    public void updateSettings(SiteSettingUpdateRequest request) {
        if (request.getDeleteIds() != null) {
            for (Long id : request.getDeleteIds()) {
                siteSettingRepository.deleteById(id);
            }
        }

        if (request.getSettings() != null) {
            for (SiteSetting setting : request.getSettings()) {
                if (setting.getId() != null) {
                    SiteSetting existing = siteSettingRepository.findById(setting.getId())
                            .orElseThrow(() -> new BusinessException("配置不存在: " + setting.getId()));
                    existing.setValue(setting.getValue());
                    siteSettingRepository.save(existing);
                } else {
                    if (setting.getNameEn() == null || setting.getType() == null) {
                        throw new BusinessException("新增配置缺少必要字段");
                    }
                    siteSettingRepository.save(setting);
                }
            }
        }

        syncToLegacySettings();
    }

    @Cacheable(value = CacheNames.SITE_INFO, key = "'public'")
    public PublicSiteInfoDTO getPublicSiteInfo() {
        PublicSiteInfoDTO info = new PublicSiteInfoDTO();
        List<SiteSetting> all = siteSettingRepository.findAllByOrderByIdAsc();

        for (SiteSetting setting : all) {
            switch (setting.getNameEn()) {
                case SiteSettingConstants.BLOG_NAME -> info.setBlogName(setting.getValue());
                case SiteSettingConstants.FOOTER_IMG_TITLE -> info.setFooterImgTitle(setting.getValue());
                case SiteSettingConstants.FOOTER_IMG_URL -> info.setFooterImgUrl(setting.getValue());
                case SiteSettingConstants.COPYRIGHT -> info.setCopyright(parseJson(setting.getValue(), CopyrightDTO.class));
                case SiteSettingConstants.BEIAN -> info.setBeian(setting.getValue());
                default -> {
                }
            }
            if (setting.getType() == SiteSettingConstants.TYPE_BADGE) {
                BadgeDTO badge = parseJson(setting.getValue(), BadgeDTO.class);
                if (badge != null) {
                    info.getBadges().add(badge);
                }
            }
        }

        if (info.getFooterImgTitle() == null || info.getFooterImgTitle().isBlank()) {
            info.setFooterImgTitle("手机看本站");
        }
        return info;
    }

    private <T> T parseJson(String json, Class<T> type) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    private void syncToLegacySettings() {
        Settings settings = settingsRepository.findById(1L).orElseGet(() -> {
            Settings created = new Settings();
            created.setId(1L);
            return created;
        });

        for (SiteSetting siteSetting : siteSettingRepository.findAllByOrderByIdAsc()) {
            if (siteSetting.getType() != SiteSettingConstants.TYPE_PROFILE
                    && siteSetting.getType() != SiteSettingConstants.TYPE_BASIC) {
                continue;
            }
            switch (siteSetting.getNameEn()) {
                case SiteSettingConstants.BLOG_NAME -> settings.setSiteName(siteSetting.getValue());
                case SiteSettingConstants.AVATAR -> settings.setAuthorAvatar(siteSetting.getValue());
                case SiteSettingConstants.NAME -> settings.setAuthorName(siteSetting.getValue());
                case SiteSettingConstants.GITHUB -> settings.setGithub(siteSetting.getValue());
                case SiteSettingConstants.TELEGRAM -> settings.setTelegram(siteSetting.getValue());
                case SiteSettingConstants.TWITTER -> settings.setTwitter(siteSetting.getValue());
                case SiteSettingConstants.GITEE -> settings.setGitee(siteSetting.getValue());
                case SiteSettingConstants.EMAIL -> settings.setEmail(normalizeEmail(siteSetting.getValue()));
                case SiteSettingConstants.BEIAN -> settings.setIcp(siteSetting.getValue());
                case SiteSettingConstants.ROLL_TEXT -> settings.setAuthorBio(extractRollText(siteSetting.getValue()));
                default -> {
                }
            }
        }

        settingsRepository.save(settings);
    }

    private String normalizeEmail(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("mailto:", "").trim();
    }

    private String extractRollText(String rollText) {
        if (rollText == null || rollText.isBlank()) {
            return "";
        }
        int start = rollText.indexOf('"');
        int end = rollText.indexOf('"', start + 1);
        if (start >= 0 && end > start) {
            return rollText.substring(start + 1, end);
        }
        return rollText;
    }
}
