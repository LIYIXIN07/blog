package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.PageResult;
import com.blog.config.CacheNames;
import com.blog.dto.response.*;
import com.blog.entity.VisitRecord;
import com.blog.repository.AccessLogRepository;
import com.blog.util.IpRegionUtils;
import com.blog.util.IpUtils;
import com.blog.util.UserAgentUtils;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategoryRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.SettingsRepository;
import com.blog.repository.TagRepository;
import com.blog.repository.VisitRecordRepository;
import com.blog.entity.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final String[] CATEGORY_COLORS = {
            "#ff9800", "#ffc107", "#00bcd4", "#2196f3", "#9c27b0", "#4caf50"
    };

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final AccessLogRepository accessLogRepository;
    private final SettingsRepository settingsRepository;
    private final TagService tagService;
    private final LogService logService;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        stats.setTodayPv(accessLogRepository.countSince(todayStart));
        stats.setTodayUv(accessLogRepository.countDistinctVisitorsSince(todayStart));

        List<CategoryStatDTO> categories = new ArrayList<>();
        var categoryRows = categoryRepository.findCategoryWithArticleCount();
        for (int i = 0; i < categoryRows.size(); i++) {
            var row = categoryRows.get(i);
            var category = (com.blog.entity.Category) row[0];
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            CategoryStatDTO item = new CategoryStatDTO();
            item.setId(category.getId());
            item.setName(category.getName());
            item.setCount((int) count);
            item.setColor(CATEGORY_COLORS[i % CATEGORY_COLORS.length]);
            categories.add(item);
        }
        stats.setCategories(categories);
        stats.setTags(tagService.getAllTags());
        stats.setTraffic(getTrafficStats(30));
        stats.setTotalArticles(articleRepository.countByStatus(1));
        stats.setTotalComments(commentRepository.count());
        Long totalViews = articleRepository.sumViewCount();
        stats.setTotalViews(totalViews != null ? totalViews : 0L);
        stats.setTotalVisitors(visitRecordRepository.count());
        stats.setCityVisitors(getCityVisitorStats());
        return stats;
    }

    public List<CityVisitorDTO> getCityVisitorStats() {
        Map<String, Long> cityMap = visitRecordRepository.findIpSources().stream()
                .map(row -> IpRegionUtils.extractCity((String) row[0], (String) row[1]))
                .filter(city -> city != null && !city.isBlank())
                .collect(Collectors.groupingBy(city -> city, Collectors.counting()));

        return cityMap.entrySet().stream()
                .map(entry -> new CityVisitorDTO(entry.getKey(), entry.getValue().intValue()))
                .sorted((a, b) -> b.getUv().compareTo(a.getUv()))
                .toList();
    }

    @Cacheable(value = CacheNames.SITE_STATS, key = "'site'")
    public SiteStatsDTO getSiteStats() {
        SiteStatsDTO stats = new SiteStatsDTO();
        Long pv = visitRecordRepository.sumTotalPv();
        stats.setTotalPv(pv != null ? pv : 0L);
        stats.setTotalUv(visitRecordRepository.count());
        stats.setTotalArticles(articleRepository.countByStatus(1));
        stats.setRunDays(calculateRunDays());
        return stats;
    }

    private int calculateRunDays() {
        Settings settings = settingsRepository.findById(1L).orElse(null);
        LocalDate startDate = LocalDate.now();
        if (settings != null) {
            if (settings.getSiteStartDate() != null) {
                startDate = settings.getSiteStartDate();
            } else if (settings.getCreatedAt() != null) {
                startDate = settings.getCreatedAt().toLocalDate();
            }
        }
        long days = ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;
        return (int) Math.max(days, 1);
    }

    public List<TrafficStatDTO> getTrafficStats(int days) {
        LocalDateTime startTime = LocalDate.now().minusDays(days - 1L).atStartOfDay();
        List<Object[]> rows = visitRecordRepository.findTrafficStats(startTime);
        Map<String, TrafficStatDTO> map = rows.stream().collect(Collectors.toMap(
                row -> String.valueOf(row[0]),
                row -> {
                    TrafficStatDTO dto = new TrafficStatDTO();
                    dto.setDate(formatDate(String.valueOf(row[0])));
                    dto.setPv(row[1] != null ? ((Number) row[1]).longValue() : 0L);
                    dto.setUv(row[2] != null ? ((Number) row[2]).longValue() : 0L);
                    return dto;
                },
                (a, b) -> a
        ));

        List<TrafficStatDTO> result = new ArrayList<>();
        DateTimeFormatter keyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String key = date.format(keyFormatter);
            TrafficStatDTO dto = map.getOrDefault(key, new TrafficStatDTO());
            dto.setDate(date.format(labelFormatter));
            if (dto.getPv() == null) {
                dto.setPv(0L);
            }
            if (dto.getUv() == null) {
                dto.setUv(0L);
            }
            result.add(dto);
        }
        return result;
    }

    @CacheEvict(value = CacheNames.SITE_STATS, key = "'site'")
    @Transactional
    public void recordVisit(String visitorUuid, String ip, String userAgent, String pageUri) {
        String os = UserAgentUtils.parseOs(userAgent);
        String browser = UserAgentUtils.parseBrowser(userAgent);
        String ipSource = IpUtils.resolveIpSource(ip);

        VisitRecord record = visitRecordRepository.findByVisitorUuid(visitorUuid)
                .orElseGet(VisitRecord::new);

        if (record.getId() == null) {
            record.setVisitorUuid(visitorUuid);
            record.setPv(1);
            record.setIp(ip);
            record.setIpSource(ipSource);
            record.setOs(os);
            record.setBrowser(browser);
            visitRecordRepository.save(record);
        } else {
            visitRecordRepository.incrementPv(visitorUuid, ip, ipSource, os, browser);
        }

        logService.recordAccess(visitorUuid, pageUri, ip, userAgent);
    }

    public PageResult<VisitRecordDTO> getVisitorRecords(Integer pageNum, Integer pageSize,
                                                        LocalDateTime startTime, LocalDateTime endTime) {
        Page<VisitRecord> page = visitRecordRepository.findByVisitTimeRange(
                startTime, endTime, PageRequest.of(pageNum - 1, pageSize));
        List<VisitRecordDTO> list = page.getContent().stream().map(this::toVisitDTO).toList();
        return new PageResult<>(list, page.getTotalElements(), pageNum, pageSize);
    }

    @Transactional
    public void deleteVisitor(Long id) {
        VisitRecord record = visitRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("访客记录不存在"));
        accessLogRepository.deleteByVisitorUuid(record.getVisitorUuid());
        visitRecordRepository.delete(record);
    }

    private VisitRecordDTO toVisitDTO(VisitRecord record) {
        VisitRecordDTO dto = new VisitRecordDTO();
        dto.setId(record.getId());
        dto.setVisitorUuid(record.getVisitorUuid());
        dto.setIp(record.getIp());
        dto.setIpSource(IpUtils.resolveIpSource(record.getIp()));
        dto.setOs(record.getOs());
        dto.setBrowser(record.getBrowser());
        dto.setFirstVisit(record.getCreatedAt());
        dto.setLastVisit(record.getUpdatedAt());
        dto.setPv(record.getPv() != null ? record.getPv() : 0);
        return dto;
    }

    private String formatDate(String rawDate) {
        if (rawDate == null || rawDate.length() < 10) {
            return rawDate;
        }
        return rawDate.substring(5, 7) + "-" + rawDate.substring(8, 10);
    }
}
