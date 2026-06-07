package com.mxster.everyphants.model.plugin.core;

import java.util.List;

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

    public abstract List<Result> query(String input);

    protected void applyPluginIcon(List<Result> results) {
        if (iconFile == null || iconFile.isEmpty()) {
            return;
        }
        for (Result r : results) {
            if (r.getIconPath() == null || r.getIconPath().isEmpty()) {
                r.setIconPath(iconFile);
            }
        }
    }
}