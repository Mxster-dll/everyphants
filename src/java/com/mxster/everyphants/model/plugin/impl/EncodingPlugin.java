package com.mxster.everyphants.model.plugin.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class EncodingPlugin extends ReactivePlugin<String> {

    public EncodingPlugin() {
        super("编码");

        parsers.add(s -> s.matches("\\d+") ? null : s);

        formatters.add(this::buildUrlEncode);
        formatters.add(this::buildBase64);
        formatters.add(this::buildUtf8Bytes);
    }

    public Result buildUrlEncode(String s) {
        String encoded;
        try {
            encoded = URLEncoder.encode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            encoded = "编码失败: " + e.getMessage();
        }
        return new Result(encoded, "URL编码", 0.1, null);
    }

    public Result buildBase64(String s) {
        String encoded = Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
        return new Result(encoded, "Base64编码", 0.1, null);
    }

    public Result buildUtf8Bytes(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02X ", b));
        }
        return new Result(hex.toString().trim(), "UTF-8 字节", 0.1, null);
    }
}
