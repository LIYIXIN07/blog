package com.blog.dto.request;

import com.blog.entity.SiteSetting;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SiteSettingUpdateRequest {

    private List<SiteSetting> settings = new ArrayList<>();
    private List<Long> deleteIds = new ArrayList<>();
}
