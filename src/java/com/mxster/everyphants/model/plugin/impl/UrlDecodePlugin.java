package com.mxster.everyphants.model.plugin.impl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class UrlDecodePlugin extends ReactivePlugin<String> {

    public UrlDecodePlugin() {
        super("URL解码");

        parsers.add(this::parseUrlEncoded);

        formatters.add(this::buildUrlDecode);
    }

    public String parseUrlEncoded(String s) {
        try {
            String decoded = URLDecoder.decode(s, StandardCharsets.UTF_8);
            if (decoded.equals(s)) {
                return null;
            }
            return decoded;
        } catch (Exception e) {
            return null;
        }
    }

    public Result buildUrlDecode(String s) {
        if (isGarbled(s)) {
            return null;
        }
        return new Result(s, "URL解码", 0.1, null);
    }

    private static boolean isGarbled(String s) {
        int bad = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 0xFFFD)
                return true;
            if (c < 0x20 && c != '\t' && c != '\n' && c != '\r')
                bad++;
        }
        return bad > s.length() * 0.25;
    }
}
