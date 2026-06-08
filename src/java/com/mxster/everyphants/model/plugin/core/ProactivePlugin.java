package com.mxster.everyphants.model.plugin.core;

import com.mxster.everyphants.model.Result;

public abstract class ProactivePlugin extends Plugin {
    private Result cachedResult;

    public ProactivePlugin(String name, String iconPath) {
        super(name, iconPath);
    }

    @Override
    public final Result query(String input) {
        if (cachedResult == null) {
            cachedResult = build();
            applyPluginIcon(cachedResult);
        }
        return cachedResult;
    }

    public abstract Result build();
}
