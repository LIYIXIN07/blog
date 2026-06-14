package com.blog.service;

import com.blog.entity.Settings;
import com.blog.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 站点设置默认数据（博主信息与社交链接）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsInitService {

    public static final String DEFAULT_AVATAR = "/images/author-avatar.png";
    public static final String DEFAULT_GITHUB = "https://github.com/telF4";
    public static final String DEFAULT_TELEGRAM = "https://t.me/ghxjsksnhdjjdj";
    public static final String DEFAULT_TWITTER = "https://x.com/jien386618";
    public static final String DEFAULT_GITEE = "https://gitee.com/DollMeowOnly";

    private final SettingsRepository settingsRepository;

    @Transactional
    public void ensureDefaultSettings() {
        Settings settings = settingsRepository.findById(1L).orElseGet(() -> {
            Settings created = new Settings();
            created.setId(1L);
            created.setSiteName("个人博客");
            created.setSiteDescription("记录技术成长，分享学习心得");
            created.setAuthorName("DollMeowOnly");
            created.setAuthorBio("热爱技术，乐于分享");
            return created;
        });

        boolean updated = false;
        if (isBlank(settings.getAuthorAvatar())) {
            settings.setAuthorAvatar(DEFAULT_AVATAR);
            updated = true;
        }
        if (isBlank(settings.getGithub())) {
            settings.setGithub(DEFAULT_GITHUB);
            updated = true;
        }
        if (isBlank(settings.getTelegram())) {
            settings.setTelegram(DEFAULT_TELEGRAM);
            updated = true;
        }
        if (isBlank(settings.getTwitter())) {
            settings.setTwitter(DEFAULT_TWITTER);
            updated = true;
        }
        if (isBlank(settings.getGitee())) {
            settings.setGitee(DEFAULT_GITEE);
            updated = true;
        }
        if ("博主".equals(settings.getAuthorName())) {
            settings.setAuthorName("DollMeowOnly");
            updated = true;
        }

        settingsRepository.save(settings);
        if (updated) {
            log.info("站点设置默认博主信息已写入");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
