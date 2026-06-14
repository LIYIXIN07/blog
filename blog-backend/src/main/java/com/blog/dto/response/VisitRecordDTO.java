package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisitRecordDTO {
    private Long id;
    private String visitorUuid;
    private String ip;
    private String ipSource;
    private String os;
    private String browser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstVisit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastVisit;

    private Integer pv;
}
