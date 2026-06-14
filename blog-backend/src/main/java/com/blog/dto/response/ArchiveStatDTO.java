package com.blog.dto.response;

import lombok.Data;

@Data
public class ArchiveStatDTO {

    private Integer year;
    private Integer month;
    private String label;
    private Long count;
}
