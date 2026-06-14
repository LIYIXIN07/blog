package com.blog.dto.request;

import lombok.Data;

import java.util.List;

/**
 * 系统设置请求DTO
 */
@Data
public class SettingsRequest {

    private String siteName;
    private String siteDescription;
    private String authorName;
    private String authorAvatar;
    private String authorBio;
    private String github;
    private String telegram;
    private String twitter;
    private String gitee;
    private String email;
    private String icp;
    private java.time.LocalDate siteStartDate;
}