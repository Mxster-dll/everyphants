package com.mxster.everyphants.plugin;

import com.mxster.everyphants.model.ReactivePlugin;
import com.mxster.everyphants.model.Result;

public abstract class DecodePlugin extends ReactivePlugin<String> {
    public DecodePlugin(String name, String iconPath) {
        super(name, iconPath);
    }

    @Override
    public Result build(String s) {
        if (isUnreadableText(s)) {
            return null;
        }
        return new Result(s, super.name, 0.1);
    }

    private static boolean isUnreadableText(String s) {
        int bad = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 0xFFFD || c == 0xFFFE || c == 0xFFFF || c == 0x0000
                    || Character.isSurrogate(c)) {
                return true;
            }
            if (Character.isISOControl(c) && c != '\t' && c != '\n' && c != '\r') {
                bad++;
            }
        }
        return bad > s.length() * 0.25;
    }
}
