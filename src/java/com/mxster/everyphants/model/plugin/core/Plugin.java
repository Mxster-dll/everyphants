package com.mxster.everyphants.model.plugin.core;

public abstract class Plugin {
    protected String name;
    protected String iconPath;

    public Plugin(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }
}