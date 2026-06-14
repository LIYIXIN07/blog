package com.blog.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DashboardStatsDTO {
    private Long todayPv;
    private Long todayUv;
    private List<CategoryStatDTO> categories;
    private List<TagDTO> tags;
    private List<TrafficStatDTO> traffic;
    private Long totalArticles;
    private Long totalComments;
    private Long totalViews;
    private Long totalVisitors;
    private List<CityVisitorDTO> cityVisitors;
}
