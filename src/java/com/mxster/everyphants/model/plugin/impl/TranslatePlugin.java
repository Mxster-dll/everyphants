package com.mxster.everyphants.model.plugin.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mxster.everyphants.model.LoadingResult;
import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

import javafx.scene.paint.Color;

public class TranslatePlugin extends ReactivePlugin<String> {
    private static final String APP_ID;
    private static final String SECRET_KEY;
    private static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    static {
        String appId = null;
        String secretKey = null;
        try {
            Path settingsPath = Paths.get("src/resources/com/mxster/everyphants/data/settings.json");
            String content = Files.readString(settingsPath);
            appId = readJsonValue(content, "baidu-fanyi-appid");
            secretKey = readJsonValue(content, "baidu-fanyi-api-key");
        } catch (Exception e) {
            System.err.println("[TranslatePlugin] 无法读取 settings.json: " + e.getMessage());
        }
        APP_ID = appId != null ? appId : "";
        SECRET_KEY = secretKey != null ? secretKey : "";
    }

    private static String readJsonValue(String json, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : null;
    }

    private final Map<String, LoadingResult> cache = new ConcurrentHashMap<>();
    private String lastTitle = null;
    private String lastDisplay = null;

    public TranslatePlugin() {
        super("翻译");

        parsers.add(s -> {
            if (s == null || s.isEmpty() || s.matches("\\d+"))
                return null;
            if (isNonEnglishColor(s))
                return null;
            boolean hasLetterOrHan = false;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                        || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                    hasLetterOrHan = true;
                    break;
                }
            }
            return hasLetterOrHan ? s.trim() : null;
        });

        formatters.add(this::translateToChinese);
    }

    public Result translateToChinese(String text) {
        return cache.computeIfAbsent(text, key -> {
            String titlePrefix = lastTitle != null ? lastTitle : "翻译中";
            String displayPrefix = lastDisplay != null ? lastDisplay : key;

            LoadingResult result = new LoadingResult(titlePrefix, displayPrefix, 1, null);

            new Thread(() -> {
                try {
                    LangType lang = detectLang(key);
                    String titleResult, displayResult;

                    if (lang == LangType.PURE_EN) {
                        String zh = baiduTranslate(key, "en", "zh");
                        titleResult = zh;
                        displayResult = key;
                    } else if (lang == LangType.PURE_ZH) {
                        String en = baiduTranslate(key, "zh", "en");
                        titleResult = en;
                        displayResult = key;
                    } else {
                        String zhResult = translateMixedTo(key, "zh");
                        String enResult = translateMixedTo(key, "en");
                        titleResult = zhResult;
                        displayResult = enResult;
                    }

                    result.finish(titleResult, displayResult);
                    lastTitle = titleResult;
                    lastDisplay = displayResult;
                } catch (Exception e) {
                    result.finish("翻译失败", key + " 翻译失败");
                }
                fireResultChanged();
            }, "Translate-Worker").start();

            return result;
        });
    }

    private enum LangType {
        PURE_EN, PURE_ZH, MIXED
    }

    private static LangType detectLang(String text) {
        boolean hasEn = false, hasZh = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (isCJK(c))
                hasZh = true;
            else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
                hasEn = true;
            if (hasEn && hasZh)
                return LangType.MIXED;
        }
        if (hasZh)
            return LangType.PURE_ZH;
        return LangType.PURE_EN;
    }

    private static boolean isCJK(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS;
    }

    private static boolean isNonEnglishColor(String s) {
        boolean hasNonEnglish = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                hasNonEnglish = true;
                break;
            }
        }
        if (!hasNonEnglish)
            return false;
        try {
            Color.web(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String translateMixedTo(String text, String targetLang) throws Exception {
        List<String> segments = splitByLang(text);
        StringBuilder result = new StringBuilder();

        for (String seg : segments) {
            if (seg.isEmpty())
                continue;
            LangType segLang = detectLang(seg);
            if (segLang == LangType.PURE_ZH) {
                if (targetLang.equals("zh")) {
                    result.append(seg);
                } else {
                    result.append(baiduTranslate(seg, "zh", "en"));
                }
            } else {
                if (targetLang.equals("en")) {
                    result.append(seg);
                } else {
                    result.append(baiduTranslate(seg, "en", "zh"));
                }
            }
        }

        return result.toString();
    }

    private static List<String> splitByLang(String text) {
        List<String> segments = new ArrayList<>();
        if (text.isEmpty())
            return segments;

        StringBuilder current = new StringBuilder();
        Boolean currentIsZh = null;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean isZh = isCJK(c);

            if (currentIsZh == null) {
                currentIsZh = isZh;
            } else if (currentIsZh != isZh) {
                segments.add(current.toString());
                current.setLength(0);
                currentIsZh = isZh;
            }
            current.append(c);
        }

        if (current.length() > 0) {
            segments.add(current.toString());
        }

        return segments;
    }

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
