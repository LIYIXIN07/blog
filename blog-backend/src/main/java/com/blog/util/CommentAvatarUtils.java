package com.blog.util;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public final class CommentAvatarUtils {

    private CommentAvatarUtils() {
    }

    public static String resolveAvatar(String email, String qq, String existingAvatar) {
        if (existingAvatar != null && !existingAvatar.isBlank()) {
            return existingAvatar;
        }
        if (qq != null && !qq.isBlank()) {
            return "https://q1.qlogo.cn/g?b=qq&nk=" + qq.trim() + "&s=100";
        }
        if (email != null && !email.isBlank()) {
            String hash = DigestUtils.md5DigestAsHex(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
            return "https://cravatar.cn/avatar/" + hash + "?d=identicon";
        }
        return "https://cravatar.cn/avatar/?d=identicon";
    }
}
