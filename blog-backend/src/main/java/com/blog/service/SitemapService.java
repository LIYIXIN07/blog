package com.blog.service;

import com.blog.config.BlogSiteProperties;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SitemapService {

    private static final DateTimeFormatter W3C_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final BlogSiteProperties blogSiteProperties;
    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public String generateSitemap() {
        String siteUrl = normalizeSiteUrl(blogSiteProperties.getSiteUrl());
        StringBuilder xml = new StringBuilder(4096);
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        appendUrl(xml, siteUrl + "/", "daily", "1.0", null);
        appendUrl(xml, siteUrl + "/category", "weekly", "0.8", null);
        appendUrl(xml, siteUrl + "/tag", "weekly", "0.8", null);
        appendUrl(xml, siteUrl + "/archives", "weekly", "0.7", null);
        appendUrl(xml, siteUrl + "/moments", "weekly", "0.6", null);
        appendUrl(xml, siteUrl + "/friends", "monthly", "0.5", null);
        appendUrl(xml, siteUrl + "/about", "monthly", "0.6", null);

        for (Category category : categoryRepository.findAllByOrderBySortOrderAsc()) {
            appendUrl(xml, siteUrl + "/category/" + category.getId(), "weekly", "0.7", null);
        }

        for (Tag tag : tagRepository.findAll()) {
            appendUrl(xml, siteUrl + "/tag/" + tag.getId(), "weekly", "0.7", null);
        }

        Set<String> archiveUrls = new LinkedHashSet<>();
        for (Object[] row : articleRepository.findArchiveStats()) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            archiveUrls.add(siteUrl + "/archives/" + year + "/" + month);
        }
        for (String archiveUrl : archiveUrls) {
            appendUrl(xml, archiveUrl, "monthly", "0.6", null);
        }

        for (Article article : articleRepository.findAllPublishedForSitemap()) {
            String lastmod = article.getUpdatedAt() != null
                    ? article.getUpdatedAt().toLocalDate().format(W3C_DATE)
                    : (article.getCreatedAt() != null
                    ? article.getCreatedAt().toLocalDate().format(W3C_DATE)
                    : null);
            appendUrl(xml, siteUrl + "/article/" + article.getId(), "weekly", "0.9", lastmod);
        }

        xml.append("</urlset>");
        return xml.toString();
    }

    private String normalizeSiteUrl(String siteUrl) {
        if (siteUrl == null || siteUrl.isBlank()) {
            return "http://localhost:3000";
        }
        return siteUrl.replaceAll("/+$", "");
    }

    private void appendUrl(StringBuilder xml, String loc, String changefreq, String priority, String lastmod) {
        xml.append("  <url>\n");
        xml.append("    <loc>").append(escapeXml(loc)).append("</loc>\n");
        if (lastmod != null && !lastmod.isBlank()) {
            xml.append("    <lastmod>").append(escapeXml(lastmod)).append("</lastmod>\n");
        }
        xml.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
        xml.append("    <priority>").append(priority).append("</priority>\n");
        xml.append("  </url>\n");
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
