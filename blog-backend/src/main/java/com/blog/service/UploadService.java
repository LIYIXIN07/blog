package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.config.UploadProperties;
import com.blog.dto.response.UploadResponse;
import com.blog.entity.ImageBedConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");
    private static final long MAX_SIZE = 10 * 1024 * 1024L;

    private final ImageBedService imageBedService;
    private final UploadProperties uploadProperties;

    public UploadResponse upload(MultipartFile file, HttpServletRequest request) {
        validateFile(file);
        ImageBedConfig active = imageBedService.getActiveConfig();
        return switch (active.getType()) {
            case "local" -> uploadLocal(file, imageBedService.getConfigMap(active), request);
            default -> throw new BusinessException("当前图床类型暂不支持上传，请先在图床管理中启用本地图床");
        };
    }

    private UploadResponse uploadLocal(MultipartFile file, Map<String, String> config, HttpServletRequest request) {
        String extension = getExtension(file.getOriginalFilename());
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension;

        Path targetDir = Paths.get(uploadProperties.getDir(), datePath.split("/")).toAbsolutePath().normalize();
        try {
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(filename);
            file.transferTo(targetFile.toFile());
        } catch (IOException e) {
            log.error("图片保存失败", e);
            throw new BusinessException("图片保存失败");
        }

        String urlPrefix = resolveUrlPrefix(config);
        String publicPath = urlPrefix + "/" + datePath + "/" + filename;
        publicPath = publicPath.replaceAll("(?<!:)//+", "/");
        String url = buildPublicUrl(publicPath, config, request);
        return new UploadResponse(url, filename);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("图片大小不能超过 10MB");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("仅支持 jpg、png、gif、webp、bmp、svg 格式图片");
        }
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }
    }

    private String getExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            throw new BusinessException("无法识别图片格式");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String resolveUrlPrefix(Map<String, String> config) {
        String configured = config.get("storagePath");
        if (StringUtils.hasText(configured)) {
            return configured.startsWith("/") ? configured : "/" + configured;
        }
        String fallback = uploadProperties.getUrlPrefix();
        return fallback.startsWith("/") ? fallback : "/" + fallback;
    }

    private String buildPublicUrl(String publicPath, Map<String, String> config, HttpServletRequest request) {
        String domain = config.get("domain");
        if (StringUtils.hasText(domain)) {
            String normalizedDomain = domain.trim().replaceAll("/+$", "");
            String normalizedPath = publicPath.startsWith("/") ? publicPath : "/" + publicPath;
            return normalizedDomain + normalizedPath;
        }
        String contextPath = request.getContextPath();
        return request.getScheme() + "://" + request.getServerName()
                + (isDefaultPort(request) ? "" : ":" + request.getServerPort())
                + contextPath
                + publicPath;
    }

    private boolean isDefaultPort(HttpServletRequest request) {
        int port = request.getServerPort();
        return port == 80 || port == 443;
    }
}
