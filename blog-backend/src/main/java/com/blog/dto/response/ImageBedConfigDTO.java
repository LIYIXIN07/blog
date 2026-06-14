package com.blog.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class ImageBedConfigDTO {

    private String type;
    private String typeName;
    private Boolean enabled;
    private Map<String, String> config;
}
