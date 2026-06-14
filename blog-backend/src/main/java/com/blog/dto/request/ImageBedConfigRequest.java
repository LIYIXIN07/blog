package com.blog.dto.request;

import lombok.Data;

import java.util.Map;

@Data
public class ImageBedConfigRequest {

    private Boolean enabled;
    private Map<String, String> config;
}
