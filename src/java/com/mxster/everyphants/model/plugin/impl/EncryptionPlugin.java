package com.mxster.everyphants.model.plugin.impl;

import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

import javafx.scene.paint.Color;

public abstract class EncryptionPlugin extends ReactivePlugin<String> {
    public EncryptionPlugin() {
        super("加密", "加密.png");
    }

    @Override
    public String parse(String s) {
        if (s.matches("\\d+")
                || isNonEnglishColor(s)) {
            return null;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                return null;
            }
        }
        return s;
    }

    public static boolean isNonEnglishColor(String s) {
        boolean hasNonEnglish = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                hasNonEnglish = true;
                break;
            }
        }
        if (!hasNonEnglish) {
            return false;
        }
        try {
            Color.web(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
