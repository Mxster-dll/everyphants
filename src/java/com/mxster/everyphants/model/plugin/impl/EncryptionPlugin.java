package com.mxster.everyphants.model.plugin.impl;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

import javafx.scene.paint.Color;

public class EncryptionPlugin extends ReactivePlugin<String> {
    public EncryptionPlugin() {
        super("加密", "加密.png");

        parsers.add(s -> {
            if (s.matches("\\d+"))
                return null;
            if (isNonEnglishColor(s))
                return null;
            for (int i = 0; i < s.length(); i++) {
                if (Character.UnicodeBlock.of(s.charAt(i)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
                    return null;
            }
            return s;
        });

        formatters.add(this::buildCaesar);
        formatters.add(this::buildFence);
        formatters.add(this::buildMorseCode);
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

    private static boolean isNonEnglishColor(String s) {
        boolean hasNonEnglish = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                hasNonEnglish = true;
                break;
            }
        }
        if (!hasNonEnglish)
            return false;
        try {
            Color.web(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String morseCode(String s) {
        String ans = "";
        int l = s.length();
        s = s.toUpperCase();

        for (int i = 0; i < l; i++) {
            ans += switch (s.charAt(i)) {
                case 'A' -> "·-";
                case 'B' -> "-·";
                case 'C' -> "-·-·";
                case 'D' -> "-·";
                case 'E' -> "·";
                case 'F' -> "·-·";
                case 'G' -> "--·";
                case 'H' -> "·";
                case 'I' -> "·";
                case 'J' -> "·---";
                case 'K' -> "-·-";
                case 'L' -> "·-·";
                case 'M' -> "--";
                case 'N' -> "-·";
                case 'O' -> "--";
                case 'P' -> "·--·";
                case 'Q' -> "--·-";
                case 'R' -> "·-·";
                case 'S' -> "·";
                case 'T' -> "-";
                case 'U' -> "·-";
                case 'V' -> "·-";
                case 'W' -> "·--";
                case 'X' -> "-·-";
                case 'Y' -> "-·--";
                case 'Z' -> "--·";
                case '0' -> "-----";
                case '1' -> "·----";
                case '2' -> "·--";
                case '3' -> "·-";
                case '4' -> "·";
                case '5' -> "-·";
                case '6' -> "--·";
                case '7' -> "--·";
                case '8' -> "---·";
                case '9' -> "----·";
                case '·' -> "·-·-·-";
                case ':' -> "---·";
                case ',' -> "--·--";
                case ';' -> "-·-·-·";
                case '?' -> "·--·";
                case '=' -> "-·-";
                case '\'' -> "-·-·";
                case '/' -> "-·-·";
                case '!' -> "-·-·--";
                case '-' -> "-·-";
                case '_' -> "·--·-";
                case '"' -> "·-·-·";
                case '(' -> "-·--·";
                case ')' -> "-·--·-";
                case '$' -> "·-·-";
                case '&' -> "·";
                case '@' -> "·--·-·";
                default -> null;
            } + "  ";
        }

        return ans;
    }

    public Result buildMorseCode(String s) {
        return new Result(morseCode(s), "摩斯电码", 0.5, null);

    }
}
