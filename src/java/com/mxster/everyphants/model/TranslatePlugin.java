package com.mxster.everyphants.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

/**
 * 翻译插件 —— 使用百度翻译 API
 * <p>
 * 使用方式：输入 "翻译 hello" 或 "fy hello" 即可将文本翻译为中文。
 * <p>
 * 使用前请先到 https://fanyi-api.baidu.com/ 注册并获取 APP_ID 和 SECRET_KEY，
 * 替换下方常量即可。
 */
public class TranslatePlugin extends Plugin<String> {

    // ═══════════════════════════════════════════
    // 百度翻译 API 配置 —— 请替换为你自己的
    // ═══════════════════════════════════════════
    private static final String APP_ID = "20250809002427356";
    private static final String SECRET_KEY = "AMgWkgVaYz3tzUGsd_9_";
    private static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    public TranslatePlugin() {
        super("翻译", null);

        // 解析器：匹配 "翻译 xxx" 或 "fy xxx"
        parsers.add(this::parseTranslateCommand);

        // 格式化器：调用 API 翻译为中文
        formatters.add(this::translateToChinese);
    }

    /**
     * 解析翻译命令，提取待翻译文本。
     * 支持格式：翻译 &lt;文本&gt; 或 fy &lt;文本&gt;
     */
    public String parseTranslateCommand(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        String text = input.trim();

        // 匹配 "翻译 " 前缀
        if (text.startsWith("翻译 ")) {
            String content = text.substring(3).trim();
            return content.isEmpty() ? null : content;
        }

        // 匹配 "fy " 前缀
        if (text.startsWith("fy ")) {
            String content = text.substring(3).trim();
            return content.isEmpty() ? null : content;
        }

        return null;
    }

    /**
     * 调用百度翻译 API，将文本翻译为中文
     */
    public Result translateToChinese(String text) {
        try {
            String translated = baiduTranslate(text, "auto", "zh");
            return new Result(translated, text, 1, null);
        } catch (Exception e) {
            return new Result("翻译失败: " + e.getMessage(), null, 0, null);
        }
    }

    /**
     * 调用百度翻译 API
     *
     * @param query 待翻译文本
     * @param from  源语言（auto 为自动检测）
     * @param to    目标语言
     * @return 翻译结果
     */
    private String baiduTranslate(String query, String from, String to) throws Exception {
        String salt = Integer.toString(new Random().nextInt(100000));
        String sign = md5(APP_ID + query + salt + SECRET_KEY);

        String params = "q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&from=" + from
                + "&to=" + to
                + "&appid=" + APP_ID
                + "&salt=" + salt
                + "&sign=" + sign;

        URL url = URI.create(API_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(params.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        String json = response.toString();
        return extractDstFromJson(json);
    }

    /**
     * 从百度翻译 API 返回的 JSON 中提取翻译结果。
     * 响应格式:
     * {"from":"en","to":"zh","trans_result":[{"src":"hello","dst":"\u4f60\u597d"}]}
     */
    private String extractDstFromJson(String json) {
        String marker = "\"dst\":\"";
        int start = json.indexOf(marker);
        if (start == -1) {
            // 检查是否有错误信息
            String errorMarker = "\"error_msg\":\"";
            int errorStart = json.indexOf(errorMarker);
            if (errorStart != -1) {
                int errorEnd = json.indexOf("\"", errorStart + errorMarker.length());
                return "API 错误: " + decodeUnicodeEscapes(
                        json.substring(errorStart + errorMarker.length(), errorEnd));
            }
            return "解析翻译结果失败";
        }
        start += marker.length();
        int end = json.indexOf("\"", start);
        if (end == -1) {
            return "解析翻译结果失败";
        }
        String raw = json.substring(start, end);
        return decodeUnicodeEscapes(raw);
    }

    /**
     * 将字符串中的 \\uXXXX Unicode 转义序列解码为实际字符。
     * 例如 "\\u4f60" → "你"
     */
    private String decodeUnicodeEscapes(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\' && i + 5 < s.length() && s.charAt(i + 1) == 'u') {
                String hex = s.substring(i + 2, i + 6);
                try {
                    sb.append((char) Integer.parseInt(hex, 16));
                    i += 6;
                    continue;
                } catch (NumberFormatException e) {
                    // 不是合法的 Unicode 转义，保持原样
                }
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }

    /**
     * MD5 哈希
     */
    private String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
