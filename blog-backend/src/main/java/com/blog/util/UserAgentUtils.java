package com.blog.util;

/**
 * User-Agent 解析
 */
public final class UserAgentUtils {

    private UserAgentUtils() {
    }

    public static String parseOs(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "未知";
        }
        String ua = userAgent;
        if (ua.contains("Windows NT 10.0")) {
            return "Windows 10";
        }
        if (ua.contains("Windows NT 6.3")) {
            return "Windows 8.1";
        }
        if (ua.contains("Windows NT 6.1")) {
            return "Windows 7";
        }
        if (ua.contains("Windows")) {
            return "Windows";
        }
        if (ua.contains("Mac OS X")) {
            int start = ua.indexOf("Mac OS X");
            int end = ua.indexOf(")", start);
            if (end > start) {
                return ua.substring(start, end).replace('_', '.');
            }
            return "Mac OS";
        }
        if (ua.contains("Android")) {
            int start = ua.indexOf("Android");
            int end = ua.indexOf(";", start);
            if (end > start) {
                return ua.substring(start, end);
            }
            return "Android";
        }
        if (ua.contains("iPhone") || ua.contains("iPad")) {
            return "iOS";
        }
        if (ua.contains("Linux")) {
            return "Linux";
        }
        return "未知";
    }

    public static String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "未知";
        }
        String ua = userAgent;
        if (ua.contains("MicroMessenger")) {
            return extractVersion(ua, "MicroMessenger", "WeChat");
        }
        if (ua.contains("Edg/")) {
            return extractVersion(ua, "Edg/", "Edge");
        }
        if (ua.contains("Chrome/")) {
            return extractVersion(ua, "Chrome/", "Chrome");
        }
        if (ua.contains("Firefox/")) {
            return extractVersion(ua, "Firefox/", "Firefox");
        }
        if (ua.contains("Safari/") && ua.contains("Version/")) {
            return extractVersion(ua, "Version/", "Safari");
        }
        if (ua.contains("Safari/")) {
            return "Safari";
        }
        return "未知";
    }

    private static String extractVersion(String ua, String marker, String name) {
        int start = ua.indexOf(marker);
        if (start < 0) {
            return name;
        }
        start += marker.length();
        int end = ua.indexOf(' ', start);
        if (end < 0) {
            end = ua.indexOf(')', start);
        }
        if (end < 0) {
            end = ua.length();
        }
        String version = ua.substring(start, end).trim();
        int dot = version.indexOf('.');
        if (dot > 0) {
            int secondDot = version.indexOf('.', dot + 1);
            if (secondDot > 0) {
                version = version.substring(0, secondDot);
            }
        }
        return name + " " + version;
    }
}
