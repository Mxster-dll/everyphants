package com.mxster.everyphants.model.plugin.impl;

import java.nio.charset.StandardCharsets;

public class Utf8DecodePlugin extends DecodePlugin {
    public Utf8DecodePlugin() {
        super("UTF-8解码", "解码.png");
    }

    @Override
    public String parse(String s) {
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
}
