package com.mxster.everyphants.plugin;

import com.mxster.everyphants.model.Result;

public class CaesarEncryptionPlugin extends EncryptionPlugin {
    private static final int CAESAR_SHIFT = 3;

    public static String caesar(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                sb.append((char) ((c - 'a' + CAESAR_SHIFT) % 26 + 'a'));
            } else if (c >= 'A' && c <= 'Z') {
                sb.append((char) ((c - 'A' + CAESAR_SHIFT) % 26 + 'A'));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public Result build(String s) {
        return new Result(caesar(s), "凯撒加密(3)", 0.5);
    }
}
