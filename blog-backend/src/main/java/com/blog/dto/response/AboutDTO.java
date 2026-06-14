package com.blog.dto.response;

import lombok.Data;

@Data
public class AboutDTO {

    private String title;
    private String musicId;
    private String content;
    private Boolean commentEnabled;
}
