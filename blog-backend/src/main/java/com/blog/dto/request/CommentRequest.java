package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {

    private Long id;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    private String email;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private String avatar;

    private String website;

    private String ip;
}
