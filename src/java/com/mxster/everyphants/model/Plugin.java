package com.mxster.everyphants.model;

public abstract class Plugin {
    protected String name;
    protected String iconFile;

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
}