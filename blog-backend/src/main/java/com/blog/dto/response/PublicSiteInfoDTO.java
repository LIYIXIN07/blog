package com.blog.dto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PublicSiteInfoDTO {

    private String blogName;
    private String footerImgTitle = "手机看本站";
    private String footerImgUrl;
    private CopyrightDTO copyright;
    private String beian;
    private List<BadgeDTO> badges = new ArrayList<>();
}
