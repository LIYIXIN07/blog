package com.blog.config;

import com.blog.common.result.Result;
import com.blog.service.RateLimitService;
import com.blog.util.IpUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * 公开写接口限流，防止刷接口
 */
@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        String ip = IpUtils.getClientIp(request);
        String method = request.getMethod();
        String key = method + ":" + path + ":" + ip;

        if (!checkLimit(path, method, key)) {
            response.setStatus(429);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), Result.error(429, "请求过于频繁，请稍后再试"));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkLimit(String path, String method, String key) {
        if ("POST".equals(method) && path.equals("/auth/login")) {
            return rateLimitService.tryAcquire(key, 10, Duration.ofMinutes(1));
        }
        if ("POST".equals(method) && path.equals("/comments/public")) {
            return rateLimitService.tryAcquire(key, 8, Duration.ofMinutes(1));
        }
        if ("POST".equals(method) && path.equals("/statistics/visit")) {
            return rateLimitService.tryAcquire(key, 120, Duration.ofMinutes(1));
        }
        if ("POST".equals(method) && path.matches("/articles/\\d+/view")) {
            return rateLimitService.tryAcquire(key, 60, Duration.ofMinutes(1));
        }
        return true;
    }
}
