package com.mxster.everyphants.model.plugin.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class Base64DecodePlugin extends ReactivePlugin<String> {
    public Base64DecodePlugin() {
        super("Base64解码", "解码.png");

        formatters.add(this::buildBase64Decode);
    }

    @Override
    public String parse(String s) {
        byte[] decoded = Base64.getDecoder().decode(s.trim());
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public Result buildBase64Decode(String s) {
        int bad = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 0xFFFD || c == 0xFFFE || c == 0xFFFF || c == 0x0000) {
                return null;
            }
            if (Character.isISOControl(c) && c != '\t' && c != '\n' && c != '\r') {
                bad++;
            }
            if (Character.isSurrogate(c)) {
                return null;
            }
        }
        if (bad > s.length() * 0.25) {
            return null;
        }
        return new Result(s, "Base64解码", 0.1);
    }
}
