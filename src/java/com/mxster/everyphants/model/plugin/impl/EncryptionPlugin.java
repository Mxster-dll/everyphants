package com.mxster.everyphants.model.plugin.impl;

import java.util.function.Function;

import com.mxster.everyphants.model.Result;
import com.mxster.everyphants.model.plugin.core.ReactivePlugin;

public class EncryptionPlugin extends ReactivePlugin<String> {
    public EncryptionPlugin() {
        super("加密", null);

        parsers.add(Function.identity());

        formatters.add(this::buildCaesar);
        formatters.add(this::buildFence);
    }

    public static String caesar(String s) {
        int l = s.length();
        String s1 = "";

        for (int i = 0; i < l; i++) {
            s1 += s.charAt(i) - 3;
        }

        return s1;
    }

    public Result buildCaesar(String s) {
        return new Result(caesar(s), "凯撒加密(3)", 0.5, null);
    }

    public static String fence(String s) {
        int l = s.length();
        String s1 = "";
        String s2 = "";

        for (int i = 0; i < l / 2; i++) {
            s1 += s.charAt(i);
        }
        for (int i = l / 2; i < l; i++) {
            s2 += s.charAt(i);
        }

        String s3 = "";
        for (int i = 0; i < l / 2; i++) {
            if (i % 2 == 0)
                s3 += s1.charAt(i);
            else
                s3 += s2.charAt(i);
        }
        return s3;
    }

    public Result buildFence(String s) {
        return new Result(fence(s), "栅栏加密", 0.5, null);
    }

}
