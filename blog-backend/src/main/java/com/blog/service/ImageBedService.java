package com.blog.service;

import com.blog.common.exception.BusinessException;
import com.blog.dto.request.ImageBedConfigRequest;
import com.blog.dto.response.ImageBedConfigDTO;
import com.blog.entity.ImageBedConfig;
import com.blog.repository.ImageBedConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImageBedService {

    private static final Map<String, String> TYPE_NAMES = Map.of(
            "local", "配置",
            "github", "GitHub",
            "upyun", "又拍云",
            "tencent", "腾讯云"
    );

    private static final List<String> DEFAULT_TYPES = List.of("local", "github", "upyun", "tencent");
    private static final Set<String> SENSITIVE_KEYS = Set.of("token", "password", "secretKey", "secretId");
    private static final String MASK = "******";

    private final ImageBedConfigRepository imageBedConfigRepository;
    private final ObjectMapper objectMapper;
    private final LogService logService;

    public List<ImageBedConfigDTO> getAllConfigs() {
        ensureDefaults();
        return imageBedConfigRepository.findAll().stream()
                .sorted((a, b) -> DEFAULT_TYPES.indexOf(a.getType()) - DEFAULT_TYPES.indexOf(b.getType()))
                .map(this::toDTO)
                .toList();
    }

    public ImageBedConfigDTO getConfig(String type) {
        ensureDefaults();
        ImageBedConfig config = imageBedConfigRepository.findByType(type)
                .orElseThrow(() -> new BusinessException("图床配置不存在"));
        return toDTO(config);
    }

    public ImageBedConfig getActiveConfig() {
        ensureDefaults();
        return imageBedConfigRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getEnabled()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("未启用图床，请先在图床管理中启用本地图床"));
    }

    public Map<String, String> getConfigMap(ImageBedConfig config) {
        return fromJson(config.getConfigJson());
    }

    @Transactional
    public void updateConfig(String type, ImageBedConfigRequest request, String username) {
        ensureDefaults();
        if (!TYPE_NAMES.containsKey(type)) {
            throw new BusinessException("不支持的图床类型");
        }

        ImageBedConfig config = imageBedConfigRepository.findByType(type)
                .orElseThrow(() -> new BusinessException("图床配置不存在"));

        if (Boolean.TRUE.equals(request.getEnabled())) {
            imageBedConfigRepository.findAll().forEach(item -> {
                if (!item.getType().equals(type)) {
                    item.setEnabled(false);
                    imageBedConfigRepository.save(item);
                }
            });
        }

        config.setEnabled(request.getEnabled() != null ? request.getEnabled() : config.getEnabled());
        if (request.getConfig() != null) {
            Map<String, String> merged = mergeConfig(fromJson(config.getConfigJson()), request.getConfig());
            config.setConfigJson(toJson(merged));
        }
        imageBedConfigRepository.save(config);

        logService.recordOperation(username, "图床管理", "更新" + TYPE_NAMES.get(type) + "图床配置", null, null);
    }

    public void ensureDefaults() {
        for (String type : DEFAULT_TYPES) {
            imageBedConfigRepository.findByType(type).orElseGet(() -> {
                ImageBedConfig config = new ImageBedConfig();
                config.setType(type);
                config.setEnabled("local".equals(type));
                config.setConfigJson(toJson(defaultConfig(type)));
                return imageBedConfigRepository.save(config);
            });
        }
    }

    private Map<String, String> defaultConfig(String type) {
        Map<String, String> config = new LinkedHashMap<>();
        switch (type) {
            case "github" -> {
                config.put("repo", "");
                config.put("token", "");
                config.put("branch", "main");
                config.put("path", "images");
            }
            case "upyun" -> {
                config.put("bucket", "");
                config.put("operator", "");
                config.put("password", "");
                config.put("domain", "");
            }
            case "tencent" -> {
                config.put("secretId", "");
                config.put("secretKey", "");
                config.put("bucket", "");
                config.put("region", "");
                config.put("domain", "");
            }
            default -> {
                config.put("storagePath", "/upload");
                config.put("domain", "");
            }
        }
        return config;
    }

    private ImageBedConfigDTO toDTO(ImageBedConfig config) {
        ImageBedConfigDTO dto = new ImageBedConfigDTO();
        dto.setType(config.getType());
        dto.setTypeName(TYPE_NAMES.getOrDefault(config.getType(), config.getType()));
        dto.setEnabled(config.getEnabled());
        dto.setConfig(maskConfig(fromJson(config.getConfigJson())));
        return dto;
    }

    private Map<String, String> mergeConfig(Map<String, String> current, Map<String, String> incoming) {
        Map<String, String> merged = new LinkedHashMap<>(current);
        incoming.forEach((key, value) -> {
            if (SENSITIVE_KEYS.contains(key) && MASK.equals(value)) {
                return;
            }
            merged.put(key, value);
        });
        return merged;
    }

    private Map<String, String> maskConfig(Map<String, String> config) {
        Map<String, String> masked = new LinkedHashMap<>();
        config.forEach((key, value) -> {
            if (SENSITIVE_KEYS.contains(key) && value != null && !value.isBlank()) {
                masked.put(key, MASK);
            } else {
                masked.put(key, value);
            }
        });
        return masked;
    }

    private String toJson(Map<String, String> config) {
        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new BusinessException("配置序列化失败");
        }
    }

    private Map<String, String> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return new LinkedHashMap<>();
        }
    }
}
