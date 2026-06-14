package com.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentSubmitRequest {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 250, message = "评论内容不能超过250字")
    private String content;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称过长")
    private String nickname;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    private String website;

    private Boolean notice = false;

    @NotNull(message = "页面类型不能为空")
    private Integer pageType;

    private Long articleId;

    /** 顶级评论传 -1 或 null */
    private Long parentId;
}
