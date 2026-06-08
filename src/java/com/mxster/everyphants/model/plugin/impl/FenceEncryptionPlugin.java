package com.mxster.everyphants.model.plugin.impl;

import com.mxster.everyphants.model.Result;

public class FenceEncryptionPlugin extends EncryptionPlugin {
    public static String fence(String s) {
        StringBuilder rail1 = new StringBuilder();
        StringBuilder rail2 = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            (i % 2 == 0 ? rail1 : rail2).append(s.charAt(i));
        }
        return rail1.append(rail2).toString();
    }

    @Override
    public Result build(String s) {
        return new Result(fence(s), "栅栏加密(2)", 0.5);
    }
}
