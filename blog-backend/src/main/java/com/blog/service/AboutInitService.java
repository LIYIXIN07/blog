package com.blog.service;

import com.blog.entity.AboutPage;
import com.blog.repository.AboutPageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 关于页默认数据（与 NBlog 对齐：Childhood Dreams - Gaminl）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AboutInitService {

    /** 网易云单曲 ID：Childhood Dreams - Gaminl */
    public static final String DEFAULT_MUSIC_ID = "1473552803";

    /** 旧版误填 ID（实为《热勇》- 栗先达） */
    private static final String LEGACY_MUSIC_ID = "423015580";

    private final AboutPageRepository aboutPageRepository;

    @Transactional
    public void ensureDefaultAboutData() {
        AboutPage page = aboutPageRepository.findById(1L).orElseGet(() -> {
            AboutPage created = new AboutPage();
            created.setId(1L);
            created.setTitle("关于我");
            created.setContent("");
            created.setMusicId(DEFAULT_MUSIC_ID);
            created.setCommentEnabled(true);
            return aboutPageRepository.save(created);
        });

        boolean updated = false;
        if (page.getMusicId() == null || page.getMusicId().isBlank()) {
            page.setMusicId(DEFAULT_MUSIC_ID);
            updated = true;
        } else if (LEGACY_MUSIC_ID.equals(page.getMusicId())) {
            page.setMusicId(DEFAULT_MUSIC_ID);
            updated = true;
        }
        if (page.getCommentEnabled() == null) {
            page.setCommentEnabled(true);
            updated = true;
        }
        if (updated) {
            aboutPageRepository.save(page);
            log.info("关于页默认音乐已设置: musicId={}", DEFAULT_MUSIC_ID);
        }
    }
}
