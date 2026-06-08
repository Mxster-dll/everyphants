package com.mxster.everyphants.model.plugin.core;

import com.mxster.everyphants.model.Result;

public abstract class Plugin {
    protected String name;
    protected String iconFile;

    public Plugin(String name) {
        this.name = name;
    }

    public Plugin(String name, String iconPath) {
        this.name = name;
        this.iconFile = iconPath;
    }

    public String getName() {
        return name;
    }

    public String getIconFile() {
        return iconFile;
    }

    public abstract Result query(String input);

    protected void applyPluginIcon(Result r) {
        if (iconFile == null || iconFile.isEmpty()) {
            return;
        }
        if (r.getIconPath() == null || r.getIconPath().isEmpty()) {
            r.setIconPath(iconFile);
        }
    }

    public static boolean isReadableText(String s) {
        int bad = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 0xFFFD || c == 0xFFFE || c == 0xFFFF || c == 0x0000
                    || Character.isSurrogate(c)) {
                return false;
            }
            if (Character.isISOControl(c) && c != '\t' && c != '\n' && c != '\r') {
                bad++;
            }
        }
        return bad <= s.length() * 0.25;
    }
}