package com.blog.dto.response;

import lombok.Data;

@Data
public class SiteStatsDTO {

    private Long totalPv;
    private Long totalUv;
    private Long totalArticles;
    private Integer runDays;
}
