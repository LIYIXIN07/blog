package com.blog.service;

import com.blog.config.JwtUtils;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.response.LoginResponse;
import com.blog.dto.response.UserInfoDTO;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.util.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务类
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String ip = IpUtils.getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            logService.recordLogin(request.getUsername(), ip, userAgent, false, "用户名或密码错误");
            throw e;
        }

        String token = jwtUtils.generateToken(request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        logService.recordLogin(request.getUsername(), ip, userAgent, true, "登录成功");
        
        return new LoginResponse(token, toUserInfo(user));
    }

    /**
     * 获取当前登录用户信息
     */
    public UserInfoDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return toUserInfo(user);
    }

    /**
     * 初始化管理员用户
     */
    public void initAdminUser() {
        userRepository.findByUsername("admin").ifPresentOrElse(admin -> {
            if (admin.getAvatar() == null || admin.getAvatar().isBlank()) {
                admin.setAvatar(SettingsInitService.DEFAULT_AVATAR);
                userRepository.save(admin);
            }
        }, () -> {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setAvatar(SettingsInitService.DEFAULT_AVATAR);
            admin.setRole("ADMIN");
            admin.setStatus(1);
            userRepository.save(admin);
        });
    }

    private UserInfoDTO toUserInfo(User user) {
        UserInfoDTO userInfo = new UserInfoDTO();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(resolveAvatar(user.getAvatar()));
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());
        return userInfo;
    }

    private String resolveAvatar(String avatar) {
        if (avatar == null || avatar.isBlank()) {
            return SettingsInitService.DEFAULT_AVATAR;
        }
        return avatar;
    }
}