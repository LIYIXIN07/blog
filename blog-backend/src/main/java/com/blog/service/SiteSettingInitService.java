package com.blog.service;

import com.blog.constant.SiteSettingConstants;
import com.blog.entity.Settings;
import com.blog.entity.SiteSetting;
import com.blog.repository.SettingsRepository;
import com.blog.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteSettingInitService {

    private final SiteSettingRepository siteSettingRepository;
    private final SettingsRepository settingsRepository;

    @Transactional
    public void ensureDefaultSiteSettings() {
        if (siteSettingRepository.count() > 0) {
            return;
        }

        Settings settings = settingsRepository.findById(1L).orElse(null);
        String siteName = settings != null && settings.getSiteName() != null
                ? settings.getSiteName() : "DollMeowOnly Blog";
        String authorName = settings != null && settings.getAuthorName() != null
                ? settings.getAuthorName() : "DollMeowOnly";
        String avatar = settings != null && settings.getAuthorAvatar() != null
                ? settings.getAuthorAvatar() : SettingsInitService.DEFAULT_AVATAR;
        String github = settings != null && settings.getGithub() != null
                ? settings.getGithub() : SettingsInitService.DEFAULT_GITHUB;
        String telegram = settings != null && settings.getTelegram() != null
                ? settings.getTelegram() : SettingsInitService.DEFAULT_TELEGRAM;
        String twitter = settings != null && settings.getTwitter() != null
                ? settings.getTwitter() : SettingsInitService.DEFAULT_TWITTER;
        String gitee = settings != null && settings.getGitee() != null
                ? settings.getGitee() : SettingsInitService.DEFAULT_GITEE;
        String email = settings != null && settings.getEmail() != null
                ? settings.getEmail() : "";
        String icp = settings != null && settings.getIcp() != null && !settings.getIcp().isBlank()
                ? settings.getIcp() : SettingsInitService.DEFAULT_ICP;
        String bio = settings != null && settings.getAuthorBio() != null
                ? settings.getAuthorBio() : "热爱技术，乐于分享";

        save(SiteSettingConstants.BLOG_NAME, "博客名称", siteName, SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.WEB_TITLE_SUFFIX, "网页标题后缀", " - " + siteName, SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.FOOTER_IMG_TITLE, "页脚图片标题", "手机看本站", SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.FOOTER_IMG_URL, "页脚图片路径", "/images/qr.png", SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.COPYRIGHT, "Copyright",
                "{\"title\":\"Copyright © 2026\",\"siteName\":\"DOLLMEOWONLY BLOG\"}",
                SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.BEIAN, "ICP备案号", icp, SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.REWARD, "赞赏码", "/images/reward.jpg", SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.COMMENT_ADMIN_FLAG, "博主评论标识", "我", SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.PLAYLIST_SERVER, "播放器平台", "netease", SiteSettingConstants.TYPE_BASIC);
        save(SiteSettingConstants.PLAYLIST_ID, "播放器歌单", "", SiteSettingConstants.TYPE_BASIC);

        save(SiteSettingConstants.AVATAR, "头像", avatar, SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.NAME, "昵称", authorName, SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.ROLL_TEXT, "滚动个签", "\"" + bio + "\"", SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.GITHUB, "GitHub", github, SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.TELEGRAM, "Telegram", telegram, SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.TWITTER, "Twitter", twitter, SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.GITEE, "Gitee", gitee, SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.QQ, "QQ", "", SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.BILIBILI, "bilibili", "", SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.NETEASE, "网易云音乐", "", SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.EMAIL, "email",
                email.isBlank() ? "" : "mailto:" + email, SiteSettingConstants.TYPE_PROFILE);
        save(SiteSettingConstants.FAVORITE, "自定义",
                "{\"title\":\"关于我\",\"content\":\"" + escapeJson(bio) + "\"}",
                SiteSettingConstants.TYPE_PROFILE);

        save(SiteSettingConstants.BADGE, "徽标",
                "{\"title\":\"由 Spring Boot 强力驱动\",\"url\":\"https://spring.io/projects/spring-boot\",\"subject\":\"Powered\",\"value\":\"Spring Boot\",\"color\":\"blue\"}",
                SiteSettingConstants.TYPE_BADGE);
        save(SiteSettingConstants.BADGE, "徽标",
                "{\"title\":\"Vue.js 客户端渲染\",\"url\":\"https://cn.vuejs.org/\",\"subject\":\"SPA\",\"value\":\"Vue.js\",\"color\":\"brightgreen\"}",
                SiteSettingConstants.TYPE_BADGE);
        save(SiteSettingConstants.BADGE, "徽标",
                "{\"color\":\"lightgray\",\"subject\":\"CC\",\"title\":\"本站点采用 CC BY 4.0 国际许可协议\",\"url\":\"https://creativecommons.org/licenses/by/4.0/\",\"value\":\"BY 4.0\"}",
                SiteSettingConstants.TYPE_BADGE);

        log.info("站点设置（NBlog 风格）默认数据已初始化");
    }

    private void save(String nameEn, String nameZh, String value, int type) {
        SiteSetting setting = new SiteSetting();
        setting.setNameEn(nameEn);
        setting.setNameZh(nameZh);
        setting.setValue(value);
        setting.setType(type);
        siteSettingRepository.save(setting);
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
