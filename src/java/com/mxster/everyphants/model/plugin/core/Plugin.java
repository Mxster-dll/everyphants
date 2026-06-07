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

    /**
     * 查询结果。主动型插件可忽略 input 参数。
     */
    public abstract List<Result> query(String input);

    /**
     * 将插件图标统一应用到结果列表中。
     * 仅当结果的图标尚未设置时才赋值，避免覆盖插件自定义图标。
     */
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