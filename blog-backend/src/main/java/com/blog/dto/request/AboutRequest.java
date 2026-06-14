package com.blog.dto.request;

import lombok.Data;

@Data
public class AboutRequest {

    private String title;
    private String musicId;
    private String content;
    private Boolean commentEnabled;
}
