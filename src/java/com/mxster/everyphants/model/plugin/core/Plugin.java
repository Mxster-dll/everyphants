package com.mxster.everyphants.model.plugin.core;

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
}