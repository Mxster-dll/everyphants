package com.mxster.everyphants.model.plugin.impl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class UrlDecodePlugin extends ReactivePlugin<String> {

    public UrlDecodePlugin() {
        super("URL解码", "解码.png");

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
        int bad = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 0xFFFD || c == 0xFFFE || c == 0xFFFF || c == 0x0000)
                return null;
            if (Character.isISOControl(c) && c != '\t' && c != '\n' && c != '\r')
                bad++;
            if (Character.isSurrogate(c))
                return null;
        }
        if (bad > s.length() * 0.25)
            return null;
        return new Result(s, "URL解码", 0.1, null);
    }
}
