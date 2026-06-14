package com.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PublicCommentDTO {

    private Long id;
    private String nickname;
    private String content;
    private String avatar;
    private String website;
    private Boolean adminComment;
    private Long parentId;
    private String parentNickname;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private List<PublicCommentDTO> replies = new ArrayList<>();
}
