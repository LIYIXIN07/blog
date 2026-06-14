package com.blog.dto.response;

import lombok.Data;

/**
 * 用户信息DTO
 */
@Data
public class UserInfoDTO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String role;
}