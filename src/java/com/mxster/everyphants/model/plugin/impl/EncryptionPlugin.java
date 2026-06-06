package com.mxster.everyphants.model.plugin.impl;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class EncryptionPlugin extends ReactivePlugin<String> {
    public EncryptionPlugin() {
        super("加密");

        parsers.add(s -> {
            if (s.matches("\\d+"))
                return null;
            for (int i = 0; i < s.length(); i++) {
                if (Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                    return null;
            }
            return s;
        });

        formatters.add(this::buildCaesar);
        formatters.add(this::buildFence);
    }

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

    public Result buildCaesar(String s) {
        return new Result(caesar(s), "凯撒加密(3)", 0.5, null);
    }

    public static String fence(String s) {
        StringBuilder rail1 = new StringBuilder();
        StringBuilder rail2 = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            (i % 2 == 0 ? rail1 : rail2).append(s.charAt(i));
        }
        return rail1.append(rail2).toString();
    }

    public Result buildFence(String s) {
        return new Result(fence(s), "栅栏加密(2)", 0.5, null);
    }

}
