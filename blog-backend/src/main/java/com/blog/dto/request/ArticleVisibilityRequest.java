package com.blog.dto.request;

import lombok.Data;

/**
 * 文章可见性配置（对齐 NBlog BlogVisibility）
 */
@Data
public class ArticleVisibilityRequest {

    private Boolean appreciation;
    private Boolean isRecommend;
    private Boolean commentEnabled;
    private Boolean isTop;
    /** true=公开，false=私密 */
    private Boolean published;
    private String password;
}
