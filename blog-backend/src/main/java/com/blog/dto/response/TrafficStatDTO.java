package com.blog.dto.response;

import lombok.Data;

@Data
public class TrafficStatDTO {
    private String date;
    private Long pv;
    private Long uv;
}
