package com.mxster.everyphants.model.plugin.impl;

import java.nio.charset.StandardCharsets;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class Utf8DecodePlugin extends ReactivePlugin<String> {

    public Utf8DecodePlugin() {
        super("UTF-8解码");

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
        if (isGarbled(s)) {
            return null;
        }
        return new Result(s, "UTF-8解码", 0.1, null);
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
