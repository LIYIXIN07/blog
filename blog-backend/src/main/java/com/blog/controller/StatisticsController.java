package com.blog.controller;

import com.blog.common.result.PageResult;
import com.blog.common.result.Result;
import com.blog.dto.request.VisitTrackRequest;
import com.blog.dto.response.CityVisitorDTO;
import com.blog.dto.response.DashboardStatsDTO;
import com.blog.dto.response.SiteStatsDTO;
import com.blog.dto.response.VisitRecordDTO;
import com.blog.service.StatisticsService;
import com.blog.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public Result<DashboardStatsDTO> getDashboardStats() {
        return Result.success(statisticsService.getDashboardStats());
    }

    @PostMapping("/visit")
    public Result<Void> recordVisit(@Valid @RequestBody VisitTrackRequest request,
                                    HttpServletRequest httpRequest) {
        String ip = IpUtils.getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String pageUri = request.getPageUri();
        if (pageUri == null || pageUri.isBlank()) {
            pageUri = httpRequest.getHeader("Referer");
        }
        if (pageUri == null || pageUri.isBlank()) {
            pageUri = "/";
        }
        statisticsService.recordVisit(request.getVisitorUuid(), ip, userAgent, pageUri);
        return Result.success();
    }

    @GetMapping("/visitors")
    public Result<PageResult<VisitRecordDTO>> getVisitors(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(statisticsService.getVisitorRecords(pageNum, pageSize, startTime, endTime));
    }

    @DeleteMapping("/visitors/{id}")
    public Result<Void> deleteVisitor(@PathVariable Long id) {
        statisticsService.deleteVisitor(id);
        return Result.success();
    }

    @GetMapping("/site")
    public Result<SiteStatsDTO> getSiteStats() {
        return Result.success(statisticsService.getSiteStats());
    }

    @GetMapping("/visitor-map")
    public Result<List<CityVisitorDTO>> getVisitorMap() {
        return Result.success(statisticsService.getCityVisitorStats());
    }
}
