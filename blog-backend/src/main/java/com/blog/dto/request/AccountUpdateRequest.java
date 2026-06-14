package com.blog.dto.request;

import lombok.Data;

@Data
public class AccountUpdateRequest {

    private String nickname;
    private String email;
    private String avatar;
    private String oldPassword;
    private String newPassword;
}
