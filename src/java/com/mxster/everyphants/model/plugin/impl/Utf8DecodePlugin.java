package com.mxster.everyphants.model.plugin.impl;

import java.nio.charset.StandardCharsets;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class Utf8DecodePlugin extends ReactivePlugin<String> {

    public Utf8DecodePlugin() {
        super("UTF-8解码", "解码.png");

        parsers.add(this::parseHexBytes);

        formatters.add(this::buildUtf8Decode);
    }

    public String parseHexBytes(String s) {
        try {
            String hex = s.replaceAll("\\s+", "");
            if (hex.length() % 2 != 0) {
                return null;
            }

            byte[] bytes = new byte[hex.length() / 2];
            for (int i = 0; i < hex.length(); i += 2) {
                int high = Character.digit(hex.charAt(i), 16);
                int low = Character.digit(hex.charAt(i + 1), 16);
                if (high == -1 || low == -1) {
                    return null;
                }
                bytes[i / 2] = (byte) ((high << 4) | low);
            }

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public Result buildUtf8Decode(String s) {
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
        return new Result(s, "UTF-8解码", 0.1, null);
    }
}
