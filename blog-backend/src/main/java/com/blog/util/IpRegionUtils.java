package com.blog.util;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 基于 ip2region 的 IP 地理位置解析
 */
@Slf4j
@Component
public class IpRegionUtils {

    private static Searcher searcher;

    private static final Pattern ORG_NAME_PATTERN = Pattern.compile(
            "(?i).*(telecom|mobile|unicom|netcom|tietong|corp|inc\\.?|ltd|company|cloud|network|amazon|microsoft|google).*");

    private static final Map<String, String> PROVINCE_CAPITAL = Map.ofEntries(
            Map.entry("北京市", "北京市"),
            Map.entry("天津市", "天津市"),
            Map.entry("上海市", "上海市"),
            Map.entry("重庆市", "重庆市"),
            Map.entry("广东省", "广州市"),
            Map.entry("浙江省", "杭州市"),
            Map.entry("江苏省", "南京市"),
            Map.entry("山东省", "济南市"),
            Map.entry("四川省", "成都市"),
            Map.entry("湖北省", "武汉市"),
            Map.entry("湖南省", "长沙市"),
            Map.entry("河南省", "郑州市"),
            Map.entry("河北省", "石家庄市"),
            Map.entry("福建省", "福州市"),
            Map.entry("安徽省", "合肥市"),
            Map.entry("江西省", "南昌市"),
            Map.entry("陕西省", "西安市"),
            Map.entry("辽宁省", "沈阳市"),
            Map.entry("吉林省", "长春市"),
            Map.entry("黑龙江省", "哈尔滨市"),
            Map.entry("山西省", "太原市"),
            Map.entry("云南省", "昆明市"),
            Map.entry("贵州省", "贵阳市"),
            Map.entry("广西壮族自治区", "南宁市"),
            Map.entry("海南省", "海口市"),
            Map.entry("甘肃省", "兰州市"),
            Map.entry("青海省", "西宁市"),
            Map.entry("内蒙古自治区", "呼和浩特市"),
            Map.entry("宁夏回族自治区", "银川市"),
            Map.entry("新疆维吾尔自治区", "乌鲁木齐市"),
            Map.entry("西藏自治区", "拉萨市")
    );

    @PostConstruct
    public void init() {
        try (InputStream inputStream = new ClassPathResource("ipdb/ip2region.xdb").getInputStream()) {
            byte[] dbBytes = FileCopyUtils.copyToByteArray(inputStream);
            searcher = Searcher.newWithBuffer(dbBytes);
            log.info("ip2region 数据库加载成功");
        } catch (Exception e) {
            log.warn("ip2region 数据库加载失败，IP 来源将使用基础规则: {}", e.getMessage());
        }
    }

    /**
     * 根据 IP 解析地理位置
     */
    public static String resolveIpSource(String ip) {
        if (ip == null || ip.isBlank()) {
            return "未知";
        }

        String normalized = normalizeIp(ip);
        if (isLocalhost(normalized)) {
            return "本地";
        }
        if (isPrivateIp(normalized)) {
            return "内网IP";
        }

        if (searcher == null) {
            return "未知";
        }

        try {
            String region = searcher.search(normalized);
            return formatRegion(region);
        } catch (Exception e) {
            log.debug("IP 解析失败: {} - {}", ip, e.getMessage());
            return "未知";
        }
    }

    private static String normalizeIp(String ip) {
        String value = ip.trim();
        if (value.startsWith("::ffff:")) {
            return value.substring(7);
        }
        return value;
    }

    private static boolean isLocalhost(String ip) {
        return "127.0.0.1".equals(ip)
                || "0:0:0:0:0:0:0:1".equals(ip)
                || "::1".equals(ip);
    }

    private static boolean isPrivateIp(String ip) {
        return ip.startsWith("192.168.")
                || ip.startsWith("10.")
                || (ip.startsWith("172.") && isPrivate172(ip));
    }

    private static boolean isPrivate172(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        try {
            int second = Integer.parseInt(parts[1]);
            return second >= 16 && second <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 从 IP 或已存储的 ipSource 中提取城市名（如「深圳市」），供访客地图使用
     */
    public static String extractCity(String ip, String ipSource) {
        if (isNonGeographicSource(ipSource)) {
            return null;
        }

        // 优先解析已存储的 ipSource，与后台访客列表展示保持一致
        String cityFromSource = extractCityFromIpSource(ipSource);
        if (cityFromSource != null) {
            return cityFromSource;
        }

        if (searcher != null && ip != null && !ip.isBlank()) {
            try {
                String city = normalizeCityName(parseCityFromRegion(searcher.search(normalizeIp(ip))));
                if (isValidCityName(city)) {
                    return city;
                }
            } catch (Exception e) {
                log.debug("提取城市失败: {} - {}", ip, e.getMessage());
            }
        }

        String province = extractProvince(ipSource);
        if (province != null) {
            return PROVINCE_CAPITAL.get(province);
        }
        return null;
    }

    private static String extractCityFromIpSource(String ipSource) {
        if (ipSource == null || ipSource.isBlank()) {
            return null;
        }
        if (ipSource.contains("|")) {
            String city = normalizeCityName(parseCityFromRegion(ipSource));
            if (isValidCityName(city)) {
                return city;
            }
            return null;
        }
        String city = extractCityFromSource(ipSource);
        return isValidCityName(city) ? city : null;
    }

    private static boolean isValidCityName(String city) {
        if (city == null || city.isBlank()) {
            return false;
        }
        String core = city.endsWith("市") ? city.substring(0, city.length() - 1) : city;
        return !isIspOrOrgName(core) && !isIspOrOrgName(city);
    }

    private static boolean isIspOrOrgName(String part) {
        if (part == null || part.isBlank()) {
            return false;
        }
        String value = part.trim();
        if (ORG_NAME_PATTERN.matcher(value).matches()) {
            return true;
        }
        return value.contains("电信")
                || value.contains("移动")
                || value.contains("联通")
                || value.contains("网通")
                || value.contains("铁通")
                || value.contains("广电")
                || value.contains("教育网")
                || value.contains("公司")
                || value.contains("数据中心");
    }

    private static boolean isNonGeographicSource(String ipSource) {
        if (ipSource == null || ipSource.isBlank()) {
            return false;
        }
        return "本地".equals(ipSource)
                || "内网IP".equals(ipSource)
                || "未知".equals(ipSource);
    }

    private static String extractProvince(String ipSource) {
        if (ipSource == null || ipSource.isBlank()) {
            return null;
        }
        if (ipSource.contains("|")) {
            String[] parts = ipSource.split("\\|");
            for (String part : parts) {
                if (part == null || part.isBlank() || "0".equals(part) || "中国".equals(part)) {
                    continue;
                }
                if (part.endsWith("省") || part.endsWith("自治区") || part.endsWith("特别行政区")
                        || part.endsWith("市")) {
                    return part;
                }
            }
        }
        return null;
    }

    private static String extractCityFromSource(String ipSource) {
        if (ipSource == null || ipSource.isBlank() || isNonGeographicSource(ipSource)) {
            return null;
        }
        if (ipSource.contains("|")) {
            return normalizeCityName(parseCityFromRegion(ipSource));
        }
        if (!ipSource.startsWith("中国")) {
            return null;
        }
        String[] parts = ipSource.split("\\s+");
        if (parts.length >= 3) {
            return normalizeCityName(parts[2]);
        }
        return null;
    }

    private static String parseCityFromRegion(String region) {
        if (region == null || region.isBlank()) {
            return null;
        }
        String[] parts = region.split("\\|");

        // ip2region 原始格式：国家|区域|省份|城市|ISP
        if (parts.length >= 5 && isCityCandidate(parts[3])) {
            return parts[3];
        }

        // formatRegion 之后：国家|省份|城市|ISP
        if (parts.length >= 4 && isCityCandidate(parts[2])) {
            return parts[2];
        }

        if (parts.length >= 3 && isCityCandidate(parts[2])) {
            return parts[2];
        }

        if (parts.length >= 2 && isCityCandidate(parts[1]) && parts[1].endsWith("市")) {
            return parts[1];
        }
        return null;
    }

    private static boolean isCityCandidate(String part) {
        return isValidRegionPart(part)
                && !isProvinceName(part)
                && !isIspOrOrgName(part);
    }

    private static boolean isProvinceName(String part) {
        return part.endsWith("省")
                || part.endsWith("自治区")
                || part.endsWith("特别行政区");
    }

    private static boolean isValidRegionPart(String part) {
        return part != null && !part.isBlank() && !"0".equals(part);
    }

    private static String normalizeCityName(String city) {
        if (city == null || city.isBlank() || isIspOrOrgName(city)) {
            return null;
        }
        String value = city.trim();
        if (isIspOrOrgName(value)) {
            return null;
        }
        if (value.endsWith("市")) {
            return value;
        }
        return switch (value) {
            case "北京" -> "北京市";
            case "天津" -> "天津市";
            case "上海" -> "上海市";
            case "重庆" -> "重庆市";
            default -> value.endsWith("省") || value.endsWith("自治区") ? null : value + "市";
        };
    }

    private static String formatRegion(String region) {
        if (region == null || region.isBlank()) {
            return "未知";
        }

        String[] parts = region.split("\\|");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank() || "0".equals(part)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(part);
        }

        String result = sb.toString().trim();
        if (result.isEmpty() || "内网IP".equals(result)) {
            return "内网IP";
        }
        return result;
    }
}
