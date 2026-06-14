package com.blog.dto.response;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {

    private String token;
    private UserInfoDTO userInfo;

    public LoginResponse(String token, UserInfoDTO userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}