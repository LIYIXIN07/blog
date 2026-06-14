package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VisitTrackRequest {

    @NotBlank(message = "访客标识不能为空")
    private String visitorUuid;

    private String pageUri;
}
