package com.blog.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDTO {

    private Long id;
    private String nickname;
    private String email;
    private String content;
    private String avatar;
    private String website;
    private String ip;
    private String ipSource;
    private Boolean published;
    private Boolean adminComment;
    private Integer pageType;
    private Boolean notice;
    private Long parentId;
    private Long articleId;
    private String articleTitle;
    private String qq;
    private LocalDateTime createdAt;
    private List<CommentDTO> replies = new ArrayList<>();
}
