package com.blog.config;

/**
 * Redis 缓存命名空间
 */
public final class CacheNames {

    private CacheNames() {
    }

    public static final String SETTINGS = "settings";
    public static final String ABOUT = "about";
    public static final String FRIEND_LINKS = "friendLinks";
    public static final String CATEGORIES = "categories";
    public static final String TAGS = "tags";
    public static final String ARTICLE_LIST = "articleList";
    public static final String ARTICLE_SEARCH = "articleSearch";
    public static final String ARTICLE_LATEST = "articleLatest";
    public static final String ARTICLE_HOT = "articleHot";
    public static final String ARTICLE_RANDOM = "articleRandom";
    public static final String ARTICLE_ARCHIVES = "articleArchives";
    public static final String SITE_STATS = "siteStats";
    public static final String SITE_INFO = "siteInfo";
}
